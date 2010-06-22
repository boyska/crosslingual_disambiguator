import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import weka.core.Instances;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.core.stemmers.Stemmer;


/*
 * Since the alignment files also contains complete sentences and translation,
 * it is easier to read directly that.
 * FILE STRUCTURE:
 * It is made of triples like this below:
 * # Sentence pair ($n) source length $l_s target length $l_t alignment scor : $score
 * $phrase_ in_target_lang
 * $source_word1 ({ $corr $esponding $words }) $source_word2 ({ $corr $esponding $words }) 
 */

/*
 * Represent a single phrase, with alignment. It KNOWS NOTHING OF STEMMING
 */

class PhraseTranslation
{
	private ArrayList<String> phraseWords;
	private String translatedWord;
	
	public PhraseTranslation(ArrayList<String> aPhraseWords, String aTranslatedWord)
	{
		setPhraseWords(aPhraseWords);
		setTranslatedWord(aTranslatedWord);
	}

	private void setPhraseWords(ArrayList<String> phraseWords) {
		this.phraseWords = phraseWords;
	}

	public ArrayList<String> getPhraseWords() {
		return phraseWords;
	}

	private void setTranslatedWord(String translatedWord) {
		this.translatedWord = translatedWord;
	}

	public String getTranslatedWord() {
		return translatedWord;
	}
}

public class WordAlignment {
	private ArrayList<AlignedPhrase> phrases = new ArrayList<AlignedPhrase>();
	private Log log = LogFactory.getLog(WordAlignment.class);
	private HashSet<String> stopwordsList = new HashSet<String>();
	private Stemmer stemmer = null;
	private Instances dataSet;

	public void setStemmer(Stemmer s)
	{
		stemmer = s;
	}
	public void readStopwords(String fileName)
	{
		try {
			stopwordsList.addAll(Stopwords.read_from_txt(fileName));
		} catch (Exception e) {
			// just pass
		}
	}
	public boolean getFromGz(String fileName, String targetWord)
	{
		return getFromGz(fileName, targetWord, -1);
	}
	@SuppressWarnings("deprecation")
	public boolean getFromGz(String fileName, String targetWord, int limit)
	{
		String strLine;
		ArrayList<String> line_triple = new ArrayList<String>();

		BufferedReader gzipReader;
		Pattern word_align = Pattern.compile("(\\w+) \\(\\{(.*?)\\}\\) ");


		HashSet<String> words_list = new HashSet<String>(); //Set of ALL words: it will be the list of attributes
		ArrayList<PhraseTranslation> translations = new ArrayList<PhraseTranslation>();
		try {
			gzipReader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(fileName))));
			

			while ((strLine = gzipReader.readLine()) != null) //read-everything
			{
				line_triple.add(strLine);
				if(line_triple.size()==3) //triple finished
				{
					//TODO: match only complete words
					//TODO: stem it before doing this
					

					Matcher matcher = word_align.matcher(line_triple.get(2));
					String[] foreign_words = line_triple.get(1).split(" ");
					line_triple.clear();
					if(!strLine.contains(targetWord)) //skip it
						continue;

					ArrayList<String> e_phrase = new ArrayList<String>();
					String translation = "";
					while(matcher.find()) //each iteration is word +alignment
					{
						assert matcher.groupCount() == 2;
						String e_word = matcher.group(1).trim();
						if(e_word.equals("NULL"))
							e_word = "";
						if(stopwordsList.contains(e_word))
							continue;
						if(stemmer != null)
							e_word = stemmer.stem(e_word);
						
						e_phrase.add(e_word); 
						words_list.add(e_word);
						
						//we don't care about the alignment of non-target words
						if(!e_word.equals(targetWord))
							continue;
						
						
						//parse the { x y z } alignment part
						ArrayList<Integer> f_words = new ArrayList<Integer>();
						translation = "";
						//for each number between curly brackets
						for (String number : matcher.group(2).split(" "))
						{
							if(!number.isEmpty()) 
							{
								int n_word = Integer.parseInt(number) - 1;
								f_words.add(n_word);
								translation += foreign_words[n_word] + " ";
							}
						} // end of curly brackets for
						
					} //end of word+alignment while
					if(!translation.isEmpty()) {
						PhraseTranslation trans = new PhraseTranslation(e_phrase, translation);
						translations.add(trans);
					}
					line_triple.clear();
				} //end of triple-finished if
				if (translations.size() == limit)
					break; //stop collecting!
			}//end of the read-everything while
		} catch (Exception e) {
			log.error("Error: " + e);
			e.printStackTrace();
			return false;
		}
		
		log.info("Collected " + translations.size() + " phrases");
		//now convert the data we collected to Weka data
		//we needed to do "double passing" because we need to initialize
		//the dataset with the complete list of attributes
		
		//this will convert word to attributes: they are all "boolean"
		FastVector<Attribute> attrs = new FastVector<Attribute>();
		HashMap<String, Attribute> attrs_map = new HashMap<String, Attribute>();
		Attribute att;
		for (String word : words_list) {
			att = new Attribute(word);
			attrs.add(att);
			attrs_map.put(word, att);
		}
		
		//now we need to manage class.
		//each translation is a class, so we need to get all of them
		HashMap<String, Integer> class_map = new HashMap<String, Integer>();
		FastVector<String> classes = new FastVector<String>();
		for (PhraseTranslation phraseTranslation : translations) {
			if(!class_map.containsKey(phraseTranslation.getTranslatedWord())) 
			{	
				class_map.put(phraseTranslation.getTranslatedWord(), 
						classes.size());
				classes.add(phraseTranslation.getTranslatedWord());
			}
		}
		att = new Attribute("%class", classes);
		attrs.add(att);
		attrs_map.put("%class", att);
		dataSet = new Instances("dataset", attrs, 0);
		for (PhraseTranslation phraseTranslation : translations) {
			SparseInstance inst = new SparseInstance(attrs.size());
			//set everything to 0
			for(int i=0;i<attrs.size();i++)
				inst.setValue(i, 0);
			//set present word to 1
			for (String word : phraseTranslation.getPhraseWords())
				inst.setValue(attrs_map.get(word), 1);
			//set class of instance
			inst.setValue(attrs_map.get("%class"), 
					class_map.get(phraseTranslation.getTranslatedWord())
					);
			dataSet.add(inst);
		}
		
		return true;
	}
	public void saveToArff(String fileName)
	{
		ArffSaver saver = new ArffSaver();
		
		saver.setInstances(dataSet);
		try {
			saver.setFile(new File(fileName));
			log.info("started saving");
			saver.writeBatch();
			log.info("saved");
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Errore nella scrittura del file: " + e);
		}
	}
	public void print()
	{
		for (AlignedPhrase phrase : this.getPhrases()) {
			System.out.println(phrase.toString());
		}
	}
	//TODO: toARFF()
	
	public ArrayList<AlignedPhrase> getPhrases() {
		return phrases;
	}
	
	public Instances getDataSet()
	{
		return dataSet;
	}
}

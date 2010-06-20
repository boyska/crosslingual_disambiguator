import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * Since the alignment files also contains complete sentences and translation,
 * it is easier to read directly that.
 * FILE STRUCTURE:
 * It is made of triples like this below:
 * # Sentence pair ($n) source length $l_s target length $l_t alignment scor : $score
 * $phrase_ in_target_lang
 * $source_word1 ({ $corr $esponding $words }) $source_word2 ({ $corr $esponding $words }) 
 */
class AlignedPhrase {
	private String e_phrase;
	private String f_phrase;
	private HashMap<String, List<Integer>> align = new HashMap<String, List<Integer>>(); //one word_idx -> a set of word_idx

	public void setEnglishPhrase(String phrase) {
		this.e_phrase = phrase;
	}
	public String getEnglishPhrase() {
		return e_phrase;
	}
	public void setForeignPhrase(String phrase) {
		this.f_phrase = phrase;
	}
	public String getForeignPhrase() {
		return f_phrase;
	}

	public void alignWord(String e_word, ArrayList<Integer> f_words)
	{
		//assert f_words.containsAll(f_words);
		assert e_word != null;
		assert f_words != null;
		align.put(e_word, f_words);
	}
	
	public ArrayList<String> getAlignedWords(String e_word)
	{
		ArrayList<String> words = new ArrayList<String>();
		String[] phrase_words = getForeignPhrase().split(" ");
		for (Integer i : align.get(e_word)) {
			words.add(phrase_words[i]);
		}
		return words;
	}
	
	public String toString()
	{
		String output = "";
		for (String word : align.keySet()) {
			output += word + " => [ ";
			for (String tr_word : getAlignedWords(word)) {
				output += tr_word + ' ';
			}
			output += " ] ";
		}
		return output;
	}
}

public class Alignment {
	private ArrayList<AlignedPhrase> phrases = new ArrayList<AlignedPhrase>();
	private Log log = LogFactory.getLog(Alignment.class);
	private HashSet<String> stopwordsList = new HashSet<String>();

	public void readStopwords(String fileName)
	{
		try {
			stopwordsList.addAll(Stopwords.read_from_txt(fileName));
		} catch (Exception e) {
			// just pass
		}
	}
	public boolean getFromGz(String fileName)
	{
		return getFromGz(fileName, -1);
	}
	public boolean getFromGz(String fileName, int limit)
	{
		String strLine;
		ArrayList<String> line_triple = new ArrayList<String>();

		BufferedReader gzipReader;
		Pattern word_align = Pattern.compile("(\\w+) \\(\\{(.*?)\\}\\) ");


		try {
			gzipReader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(fileName))));
			while ((strLine = gzipReader.readLine()) != null)   
			{
				AlignedPhrase phrase_align = new AlignedPhrase();
				line_triple.add(strLine);
				if(line_triple.size()==3) //triple finished
				{
					phrase_align = new AlignedPhrase();
					phrase_align.setForeignPhrase(line_triple.get(1));

					Matcher matcher = word_align.matcher(line_triple.get(2));
					String e_phrase = "";
					while(matcher.find()) //each iteration is word +alignment
					{
						assert matcher.groupCount() == 2;
						String e_word = matcher.group(1).trim();
						if(e_word.equals("NULL"))
							e_word = "";
						if(stopwordsList.contains(e_word))
							continue;
						e_phrase += " " + e_word; 
						ArrayList<Integer> f_words = new ArrayList<Integer>();

						//parse the { x y z } alignment part
						for (String number : matcher.group(2).split(" "))
						{
							if(!number.isEmpty()) 
							{
								int n_word = Integer.parseInt(number) - 1;
								f_words.add(n_word);
							}
						}
						phrase_align.alignWord(e_word, f_words);
					} //end of phrase
					phrase_align.setEnglishPhrase(e_phrase);
					line_triple.clear();
				}
				phrases.add(phrase_align);
				if (phrases.size() == limit)
					return true;

			}//end of the read-everything while
		} catch (Exception e) {
			log.error("Error: " + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void print()
	{
		for (AlignedPhrase phrase : this.phrases) {
			System.out.println(phrase.toString());
		}
	}
}

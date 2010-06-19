import java.io.*;
import java.util.ArrayList;

import org.tartarus.snowball.SnowballStemmer;

public class Europarl
/** Represent a couple of europarl datasets
 *	It will read the files, remove stopwords, stem the words, exclude not-useful phrases.
 *	It won't care about alignments nor Weka
 *  It will assume that we're translating from english to X
 */
{
	private static String data_dir = "data/";
	private ArrayList<String> e_phrases = (ArrayList<String>) new ArrayList<String>();
	private ArrayList<String> f_phrases = (ArrayList<String>) new ArrayList<String>();
	
	public static void main()
	{
		Main.log.info("Starting to read Europarl data");
	}
	//TODO: arraylist -> collection ?
	public void read_files()
	{
		Europarl.read_file(data_dir + "it-en-EN.test.txt", this.e_phrases, true);
		Europarl.read_file(data_dir + "it-en-IT.test.txt", this.f_phrases, false);
	}
	
	public void restrict_to(String targetWord)
	{
		targetWord = Europarl.doStemming(targetWord);
		assert this.e_phrases.size() == this.f_phrases.size();
		int i;
		ArrayList<String> to_remove = new ArrayList<String>();
		for(i=0; i< this.e_phrases.size();i++)
		{
			if(!this.e_phrases.get(i).contains(targetWord)) 
			{
				to_remove.add(e_phrases.get(i));
			}
		}
		for (String string : to_remove) {
			int idx = e_phrases.indexOf(string);
			e_phrases.remove(idx);
			f_phrases.remove(idx);		
		}
		for (String string : e_phrases) {
			assert string.contains(targetWord);
		}
	}
	private static void read_file(String filename, ArrayList<String> phrases, 
			boolean do_stemming)
	{
		try
		{
			FileInputStream it_stream = new FileInputStream(filename);
			DataInputStream it_in = new DataInputStream(it_stream);
	        BufferedReader it_br = new BufferedReader(
	        		new InputStreamReader(it_in));
	        String strLine;
	        while ((strLine = it_br.readLine()) != null)   
	        {
	            // Print the content on the console
	        	strLine = Europarl.removeStopwords(strLine);
	        	strLine = Europarl.doStemming(strLine);
	        	phrases.add(strLine);
	        	//Main.log.info(strLine);
	        }
	          //Close the input stream
	          it_in.close();
	          Main.log.info(filename + ": " + phrases.size());
		}
		catch (Exception e)
		{//Catch exception if any
            Main.log.fatal("Error: " + e.getMessage());
        }
		
		
	}
	//TODO get a list of stopwords and put it as a private static List
	private static String removeStopwords(String phrase)
	{
		String word, joined_phrase;
		int i;
		String[] words = phrase.split(" ");
		ArrayList<String> new_phrase = new ArrayList<String>();
		for(i=0;i<words.length;i++)
		{
			word = words[i];
			if(!word.toLowerCase().equals("the"))
				new_phrase.add(word);
		}
		
		//TODO: there must be a better way to do this
		joined_phrase = "";
		for(String w: new_phrase)
			joined_phrase += w + " ";
		return joined_phrase;
	}

	private static String doStemming(String phrase)
	{
		String word, joined_phrase;
		int i;
		String[] words = phrase.split(" ");
		ArrayList<String> new_phrase = new ArrayList<String>();
		
		SnowballStemmer stemmer = new org.tartarus.snowball.ext.englishStemmer();
		for(i=0;i<words.length;i++)
		{
			word = words[i];
			stemmer.setCurrent(word);
			stemmer.stem();
			new_phrase.add(stemmer.getCurrent());
		}
		joined_phrase = "";
		for(String w: new_phrase)
			joined_phrase += w + " ";
		return joined_phrase;		
	}
}


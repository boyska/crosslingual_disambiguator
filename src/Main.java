import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import weka.core.stemmers.SnowballStemmer;

/** HIGH-LEVEL description
 * Read directly from alignment gz file DONE
 * * Strip out stopwords DONE
 * * Stem words
 * Alignment is converted to Instances using AlignmentClassifier
 * Instances can be converted to arff using weka.core.ArffSaver
 * Call Weka
 * Run
 * Test
 */


public class Main {
	static Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) {
		
		//GLOBAL OPTIONS
		String target_word = new String("plane");
		for(String arg: args)
		{
			if(arg.startsWith("-"))
			{
			}
			else
			{
				target_word = arg;
			}
		}
		if(!Cfg.load_config("config/configuration.properties"))
			return;
		
		
		WordAlignment align = new WordAlignment();
		
		long startTime = System.currentTimeMillis();
		align.setStemmer(new SnowballStemmer("english"));
		align.getFromGz("data/it-en.A3.final.gz", target_word, -1);
		long endTime = System.currentTimeMillis();
		Main.log.info("Read in " + (endTime - startTime)/1000.0 + " seconds");
		align.print();
		startTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		Main.log.info("Converted n " + (endTime - startTime)/1000.0 + " seconds");
		
		align.saveToArff("./data/dataset_" + target_word + ".arff");
		
	}
}
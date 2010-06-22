import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
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
	private static Log log = LogFactory.getLog(Stopwords.class);

	public static void main(String[] args) {
		WordAlignment align = new WordAlignment();
		
		long startTime = System.currentTimeMillis();
		//align.setStemmer(new SnowballStemmer());
		align.getFromGz("data/it-en.A3.final.gz", "tax", 10000);
		long endTime = System.currentTimeMillis();
		Main.log.info("Read in " + (endTime - startTime)/1000.0 + " seconds");
		align.print();
		startTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		Main.log.info("Converted n " + (endTime - startTime)/1000.0 + " seconds");
		
		align.saveToArff("./data/dataset.arff");
		
		
/*		ArffSaver saver = new ArffSaver();
		
		saver.setInstances(instances);
		try {
			saver.setFile(new File("./data/dataset.arff"));
			Main.log.info("started saving");
			saver.writeBatch();
			Main.log.info("saved");
		} catch (Exception e) {
			// TODO: handle exception
			Main.log.error("Errore nella scrittura del file: " + e);
		}*/

	}
}
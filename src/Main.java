import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** HIGH-LEVEL description
 * Read directly from alignment gz file DONE
 * * Strip out stopwords DONE
 * * Stem words
 * AlignedPhrase.toWekaLine()
 * Alignment.toWeka() that wraps on AlignedPhrase.toWekaLine()
 * Call Weka
 * Run
 * Test
 */


public class Main {
	private static Log log = LogFactory.getLog(Stopwords.class);

	public static void main(String[] args) {
		//Europarl data = new Europarl();
		//data.read_files();
		//data.restrict_to("interest");
		Alignment align = new Alignment();
		
		long startTime = System.currentTimeMillis();
		align.getFromGz("data/it-en.A3.final.gz", 10000);
		long endTime = System.currentTimeMillis();
		Main.log.info("Done in " + (endTime - startTime)/1000.0 + " seconds");
	}
}
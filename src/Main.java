import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** HIGH-LEVEL description
 * Analyzing EuroParl data (without alignment) DONE
 * Cut off stopwords DONE (TODO: get a real stopwords list)
 * Word stemming with snow-ball DONE
 * Cut off phrases that doesn't have the target word DONE
 * Convert these data in a Weka-readable format:
 * * A matrix where each column is a word. A row is a sentence.
     It is set on 1 or 0 according to the presence. 
     The y has different values based on translation
 * Ask Weka to make a classifier based on this data
 * Run it (and test it)    
 */

public class Main {

	static final protected Log log = LogFactory.getLog(Main.class);
	public static void main(String[] args) {
		Europarl data = new Europarl();
		data.read_files();
		data.restrict_to("interest");
	}
}
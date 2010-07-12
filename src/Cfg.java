import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cfg {
	private static Log log = LogFactory.getLog(Stopwords.class);
	public static PropertiesConfiguration cfg = null;
	public static boolean load_config(String filename)
	{
		File f = new File(filename);
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException exc) {
				log.warn("Can't create the file: " + exc);
			}
		}
			
		try 
		{
			cfg = new PropertiesConfiguration(filename);
			cfg.setThrowExceptionOnMissing(false);
			init_default();
			return true;
		} catch (ConfigurationException exc)//error reading: IO, file format...
		{
			log.error("Error when loading config: " + exc);
			return false;
		}
		
	}
	public static void init_default()
	{
		try {
			cfg.getInt("minimum_word_occurrencies");
		} catch (ConversionException exc)
		{
			cfg.clearProperty("minimum_word_occurrencies");
		} catch(NoSuchElementException exc){
		}
		if(!cfg.containsKey("minimum_word_occorruencies"))
			cfg.addProperty("minimum_word_occurrencies", 2);

	}
}

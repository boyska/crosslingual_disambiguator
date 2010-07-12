/*
    Copyright (C) 2010  Davide Lo Re

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
			cfg = new PropertiesConfiguration(f);
			cfg.setThrowExceptionOnMissing(false);
			init_default();
			cfg.save(f);
			
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

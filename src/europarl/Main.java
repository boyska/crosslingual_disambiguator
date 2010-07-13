package europarl;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import weka.core.stemmers.SnowballStemmer;

/** HIGH-LEVEL description
 * Read directly from alignment gz file DONE
 * * Strip out stopwords DONE
 * * Stem words
 * Alignment is converted to Instances using AlignmentClassifier
 * Instances can be converted to arff using weka.core.ArffSaver
 * Classify with Weka
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
		return;
	}
	static void show_help(String program_name)
	{
		System.out.println("Syntax: " + program_name + " [-h] [target_word]");
		System.out.println("");
		System.out.println(" -h Shows this help");
	}
}
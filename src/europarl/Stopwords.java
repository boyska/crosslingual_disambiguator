package europarl;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Stopwords {
	private static Log log = LogFactory.getLog(Stopwords.class);
	
	public static ArrayList<String> read_from_txt(String filename) 
	throws Exception
	{
		ArrayList<String> stopwordsList = new ArrayList<String>();
		try
		{
			FileInputStream stream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(stream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine;
	        while ((strLine = br.readLine()) != null)
	        {
	        	strLine = strLine.trim();
	        	if(strLine.startsWith("%")) //ignored words
	        		continue;
	        	stopwordsList.add(strLine);
	        }
		}
		catch (Exception e) {
			Stopwords.log.error("Error: " + e);
			throw e;
		}
		return stopwordsList;
	}
	
}

package classifier;
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

/** HIGH LEVEL DESCRIPTION
 * Read an ARFF file [DONE]
 * Build a classifier from it [DONE]
 * * It MUST support distributionForInstance() method
 * Run it on a test set
 * Evaluate, considering match not as a 0/1, but as a [0,1] interval
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.HNB;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.NBTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Tee;
import weka.core.converters.ArffLoader;

public class Main {
	public static void main(String[] args)
	{
		String target_word;
		for (String datafile : get_all_datafiles("data")) {
			target_word = datafile.substring(8, datafile.length()-5);
			boolean report_exist = new File("data/report_" + target_word + ".txt").exists();
			System.out.println(target_word);
			if(!report_exist)
				test(target_word);		
		}
	}
	static Collection<String> get_all_datafiles(String dirName)
	{
		File dir = new File(dirName);
		String[] files = dir.list();
		if(files==null)
			return null;
		ArrayList<String> datafiles = new ArrayList<String>();
		for(String file: files)
			if(file.startsWith("dataset_"))
				datafiles.add(file);
		return datafiles;
	}
	public static void test(String target_word)
	{
		Tee tee = new Tee(System.out);
		try {
			tee.add(new PrintStream(new FileOutputStream("data/report_" + target_word + ".txt")));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			System.err.println("Problems when opening the log file: aborting");
			e2.printStackTrace();
			System.exit(1);
		}
		
		ArffLoader data_file = new ArffLoader();
		Instances data;
		try {
			File f = new File("data/dataset_" + target_word + ".arff");
			data_file.setSource(f);
			data = data_file.getDataSet();
		} catch (IOException e) {
			System.err.println("Error while reading the dataset: ");
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		//As a test, use just first step of cross-validation
		data.setClass(data.attribute("%class"));
		Instances training = data.trainCV(10, 0);
		Instances test = data.testCV(10, 0);
		
		Classifier classifier = new NaiveBayes();
		//Classifier classifier = new HNB();
		//classifier = new J48();
		//Classifier classifier = new NBTree();
		/*IBk classifier = new IBk();
		String[] options = new String[1];
		options[0] = "-I";
		try {
			classifier.setOptions(options);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println("Error when giving options. But we won't stop");
			e1.printStackTrace();
		}*/
		try {
			classifier.buildClassifier(training);
		} catch (Exception e) {
			System.err.println("Error when building model:");
			e.printStackTrace();
			System.exit(1);
		}
		tee.println("Classifier built!");
		
		double total=0;
		for (Instance instance : test) {
			try {
				double[] evaluation = classifier.distributionForInstance(instance);
				//evaluation is an array; sum of its elements is ~1
				
				double score = evaluation[(int)instance.value(test.attribute("%class"))];
				
				/*if(score> 0.5)
					total += 1;
				else if(score > 0.2)*/
				total += score;
				score = ((long)(score*100))/100.0;
				tee.println("EVAL " + score + " " + 
						instance.stringValue(test.attribute("%class")));
			} catch (Exception e) {
				System.err.println("Error when classifying " + instance);
				e.printStackTrace();
			}
		}
		
		tee.println("SMALL REPORT:");
		tee.println("Total True: " + total);
		tee.println("#Test Instances: " + test.size());
		tee.println("Accuracy: " + total/test.size());
		
	}
}

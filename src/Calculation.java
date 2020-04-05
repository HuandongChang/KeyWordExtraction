/**
 * @author Mingyang Fan
 * @author Huandong Chang
 * @author Jun Seok Choi
 * @version March 2020
 */

package assignment3;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
//import org.apache.commons.io.*;



public class Calculation{

	/**
	 * Delete all the tags created by the Stanford Tagger from a string
	 * 
	 * @param tagged, a string of the Stanford tagger formate output
	 * @return a string, removing all the tags created by the Stanford Tagger
	 */
	public static String removeTags(String tagged){
		String pattern = "_[^\\s]+";
		String noTag=tagged.replaceAll(pattern, " ");
		String noSpace=noTag.replaceAll("\\s\\s", " ");
		return noSpace.substring(0,noSpace.length()-1);
	}


	/**
	 * Delete all the tags created by the Stanford Tagger from a list of strings
	 * 
	 * @param lst, a list of Strings with the POStags
	 * @return new list, a list of stirng without the POStags
	 */
	public static List listRemoveTags(List<String> lst){
		List<String> newList=new ArrayList<String>();
		for (int i=0;i<lst.size();i++){
			String tag=lst.get(i);
			String noTag=removeTags(tag);
			newList.add(noTag);
		}
		return newList;
	}

	/*
NEW PART BELOW: POS Pattern
	 */

	/**
	 * Check if a single word follows the POS pattern
	 * 
	 * @param str
	 * @return true, if the given string (with only one word) follows the POS pattern
	 * @return false otherwise
	 */
	public static Boolean checkSinglePOS (String str) {
		// String to be scanned to find the pattern.
		String pattern = "_[N|J]";
		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);
		// Now create matcher object.
		Matcher m = r.matcher(str);
		if (m.find())
			return true;
		else
			return false;
	}


	/**
	 * Check if a uni-, bi-, or tri- gram follows the POS pattern
	 * 
	 * @param str
	 * @return true, if the given string follows the POS pattern
	 * @return false otherwise
	 */
	public static Boolean checkPOS (String str) {
		String[] arrOfStr = str.split(" ");
		if (arrOfStr.length == 1)
			return checkSinglePOS(arrOfStr[0]);

		if (arrOfStr.length == 2)
			return (checkSinglePOS(arrOfStr[0]) && checkSinglePOS(arrOfStr[1]));

		if (arrOfStr.length == 3)
			return (checkSinglePOS(arrOfStr[0]) && checkSinglePOS(arrOfStr[2]));

		return false;
	}


	/**
	 * Check whether a lst matches POS Patterns for a single or more words and return different
	 * scores based on matching result
	 * 
	 * @param lst a list of strings
	 * @return a list of numbers, 1.66 if the string matches POS Patterns, 1.00 otherwise
	 */
	public static List<Double> POSPattern(List<String> lst){
		List<Double> newList=new ArrayList<Double>();
		for (int i=0;i<lst.size();i++){
			if(checkPOS(lst.get(i)))
				newList.add(1.66);
			else
				newList.add(1.0);
		}
		return newList;
	}


	/*
NEW PART BELOW: Calculation
Calculation Part a):Term Frequency

	 */

	/**
	 * Read a file as a string
	 * 
	 * @param fileName
	 * @return data, a string version of the file
	 * @throws Exception
	 */
	// Citation: https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
	public static String readFileAsString(String fileName)throws Exception
	{
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
	}


	/** 
	 * the frequency of a word
	 * 
	 * @param str a string
	 * @param filePath the path 
	 * @return count, the int of frequency
	 */
	public static int frequency(String str, String filePath) throws Exception{
		int count=0;
		Pattern pattern = Pattern.compile(str);
		String fileInput=readFileAsString(filePath);
		Matcher m = pattern.matcher(fileInput);
		while (m.find())
			count++;
		return count;
	}


	/** 
	 * the frequency of a list of words
	 * 
	 * @param lst 
	 * @param filePath
	 * @return the frequencies corresponding to each word in a lst
	 */
	public static List<Integer> listFrequency(List<String> lst, String filePath) throws Exception{
		List<Integer> newList=new ArrayList<Integer>();
		for (int i=0;i<lst.size();i++){
			String str=lst.get(i);
			int count=frequency(str,filePath);
			newList.add(count);
		}
		return newList;
	}

	/*
NEW PART BELOW: Calculation
Calculation Part b): Inverse Document Frequency
	 */

	/** 
	 * the proportion of files that contain the string in a directory
	 *
	 * @param lst a list of words
	 * @param directoryPath directory Path
	 * @return the inverse document Frequency corresponding to each word in a lst
	 */
	public static List<Double> inverseFrequency(List<String> lst, String directoryPath) throws Exception{
		List<Double> newList=new ArrayList<Double>();
		int totalDocuments=1460-6+1;


		for (int i=0;i<lst.size();i++){
			String str=lst.get(i);
			Pattern pattern = Pattern.compile(str);


			//Compare to each file in the directoryPath
			double num=0;

			File folder = new File(directoryPath);
			File[] fileNames = folder.listFiles();
			for(File file: fileNames){
				String fileInput = readFileAsString(file.getAbsolutePath());
				//System.out.println(fileInput);
				//String fileInput=readFileAsString(file);
				Matcher m = pattern.matcher(fileInput);

				if(m.find())
					num++;
			}

			//double inverseF= num/(double)totalDocuments;
			//newList.add(inverseF);
			newList.add(num/totalDocuments);
		}
		return newList;
	}

	/*
NEW PART BELOW: Calculation
Calculation Part c): Relative position of the first-occurrence
	 */

	/** 
	 * the first occurrence of each word in the file
	 *
	 * @param lst a list of words
	 * @param file file Path
	 * @return the first occurrence of each word in the file, index/total chars
	 */
	public static List<Double> firstOccurrence(List<String> lst, String filePath) throws Exception{
		List<Double> newList=new ArrayList<Double>();
		String content=readFileAsString(filePath);
		for (int i=0;i<lst.size();i++){
			String str=lst.get(i);
			int index=content.indexOf(str);
			double indexInverse= 1/(double)index;
			newList.add(indexInverse);
		}
		return newList;
	}


	/*
NEW PART BELOW: rankingHelper
Ranking: Top five key words
//Reference: https://beginnersbook.com/2013/12/how-to-sort-hashmap-in-java-by-keys-and-values/
	 */

	//Helper Procedure
	private static HashMap sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}




	public static List<String> rankingHelper(List<String> lst, List<Double> POSPattern, List<Integer> Criteria1, List<Double> Criteria2, List<Double> Criteria3){
		//HashMap
		HashMap<String, Double> hmap = new HashMap<String, Double>();

		for (int i=0;i<Criteria1.size();i++){
			double calc=POSPattern.get(i)*Criteria1.get(i)*Criteria2.get(i)*Criteria3.get(i);


			if(lst.get(i).split(" ").length==1)
				calc*=0.001;

			if(lst.get(i).split(" ").length==3)
				calc*=0.1;


			hmap.put(lst.get(i),calc);
		}

		HashMap<String, Double> map = sortByValues(hmap);
		Set<String> keys = map.keySet();

		List<String> keyList = new ArrayList<String>();
		keyList.addAll(keys);
		if(keyList.size()<5)
			return keyList;
		else{
			keyList=keyList.subList(keyList.size()-5,keyList.size());
			return keyList;
		}
	}

	/*
NEW PART BELOW: Ranking

	 */

	/** 
	 * Choose the top five words based on calculation in a list of keyword candidates
	 *
	 * @param lst a list of words
	 * @param filePath file Path
	 * @param directoryPath directory Path
	 * @return return the top five words based on calculation in a list of keyword candidates
	 */
	public static List<String> ranking(List<String> lst, String filePath, String directoryPath) throws Exception{
		List<String> noTag=listRemoveTags(lst);
		List<Double> POSScore=POSPattern(lst);
		List<Integer> fre=listFrequency(noTag, filePath);
		List<Double> inverseFre=inverseFrequency(noTag, directoryPath);
		List<Double> first=firstOccurrence(noTag, filePath);

		return rankingHelper(noTag,POSScore,fre,inverseFre,first);
	}




	// Main method
	public static void main(String args[]) throws Exception{

		double totalFile = 0;
		double totalScore = 0;

		File folder = new File("/Users/fmy/Desktop/assignment3/abstr");
		File[] fileNames = folder.listFiles();
		for(File file: fileNames) {
			String filePath = file.getAbsolutePath();
			String[] abstrNum = filePath.split("/");
			String abstr = abstrNum[abstrNum.length - 1];
			String[] numAbstr = abstr.split("\\."); 
			String Num = numAbstr[0];


			Stack<String> wordStack = KeyExtraction.modifyWords(filePath);
			List<String> uniList = KeyExtraction.uniGram(wordStack);
			List<String> biList = KeyExtraction.biGram(wordStack);
			List<String> triList = KeyExtraction.triGram(wordStack);

			List<String> merged = new ArrayList<String>();
			// merged.addAll(uniList);
			merged.addAll(biList);
			// merged.addAll(triList);

			List<String> finalList = ranking(merged, "/Users/fmy/Desktop/assignment3/abstr/" + abstr, "/Users/fmy/Desktop/assignment3/abstr");


			System.out.println("The standard key words listed by our code are: "); 
			for(int i = 0; i < finalList.size(); i++) {
				System.out.print(finalList.get(i) + "; ");
			}

			System.out.println(); 


			double currScore = KeyExtraction.precisionScore("/Users/fmy/Desktop/assignment3/uncontr/" + Num + ".uncontr", finalList);
			totalScore += currScore;

			System.out.println("filePath: " + filePath);
			totalFile++;
			System.out.println("Num: " + totalFile);
			System.out.println("currentScore: " + currScore);
			System.out.println("Final Score: " + totalScore / totalFile);
			System.out.println("********************************");

		}


		System.out.println("The average score is: " + totalScore / totalFile);
	}
}

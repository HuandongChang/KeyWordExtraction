/**
 * @author Mingyang Fan
 * @author Huandong Chang
 * @author Jun Seok Choi
 * @version March 2020
 */


package assignment3;

import java.util.*;
import java.util.regex.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.*;

public class KeyExtraction {

	public static MaxentTagger tagger = new MaxentTagger("/Users/fmy/Desktop/models/english-left3words-distsim.tagger");

	/**
	 * Check if the input string ends with a letter (alphabetic)
	 * 
	 * @param str
	 * @return true if the input string ends with a letter (alphabetic)
	 * @return false otherwise
	 */
	public static boolean checkLetter (String str) {

		int strLen = str.length();
		char ch = str.charAt(strLen - 1);

		return Character.isLetter(ch);
	}


	/**
	 * Convert a file into one string
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
	 * remove all the punctuation in a string
	 * @param str
	 * @return newStr, a string that delete all the punctuation
	 */
	public static String removePunc (String str) {

		int strLen = str.length();
		String newStr = "";

		for (int i = 0; i < strLen; i++) {
			char ch = str.charAt(i);

			if (Character.isLetter(ch) || Character.isSpaceChar(ch))
				newStr += ch;
		}

		return newStr;
	}



	/**
	 * Check if the given string is a stop word
	 * 
	 * @param str
	 * @return true, if the given string starts or (inclusive) ends with a stop word
	 * @return false otherwise
	 * @throws IOException
	 */
	public static Boolean checkStopWords (String str) throws IOException
	{
		Hashtable<String, Integer> h = new Hashtable<String, Integer>();       

		File file = new File("/Users/fmy/Desktop/stopwords.txt");        
		BufferedReader br = new BufferedReader(new FileReader(file));

		String st; 
		int position = 0;

		while ((st = br.readLine()) != null) {
			st = tagger.tagString(st);
			int stLen = st.length();
			st = st.substring(0, stLen - 1);
			h.put(st, position);
		}
		br.close();

		return !h.containsKey(str);

	}

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
	 * Delete all the tags created by the Stanford Tagger
	 * 
	 * @param tagged, a string of the Stanford tagger formate output
	 * @return a string, removing all the tags created by the Stanford Tagger
	 */
	public static String removeTags(String tagged){
		String pattern = "_[^\\s]+";
		String noTag=tagged.replaceAll(pattern, " ");
		String noSpace=noTag.replaceAll("\\s+", " ");
		return noSpace.substring(0,noSpace.length()-1);
	}



	/**
	 * Put each word of the given file on a stack
	 * 
	 * @param fileName, a file path
	 * @return wordStack, a stack of strings. Each string is a single word in the file
	 * @throws Exception
	 */
	public static Stack<String> modifyWords(String fileName) throws Exception {
		Stack<String> wordStack = new Stack<String>();
		try {
			String fileStr = readFileAsString(fileName);
			fileStr = removePunc(fileStr);
			String tagged = tagger.tagString(fileStr);

			Scanner myReader1 = new Scanner(tagged);
			while (myReader1.hasNext()) {
				String temp = myReader1.next();

				if (checkLetter(temp))
				{
					wordStack.push(temp);
					// System.out.println(temp);
				}
			}
			myReader1.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return wordStack;
	}

	/**
	 * Put each words in the word stack that follow the non-stop-word and POS-structure on a stack
	 * 
	 * @param wordStack, a stack of strings generated by modifyWords
	 * @return uniList, a List<String>
	 * @throws Exception
	 */
	public static List<String> uniGram(Stack<String> wordStack) throws Exception {
		Hashtable<String, Integer> uniGram = new Hashtable<String, Integer>();

		int size1 = wordStack.size();
		for (int i = 0; i < size1; i++) {
			String str = wordStack.get(i);
			if (checkStopWords(str) && !uniGram.containsKey(str))
				uniGram.put(str, 0);
		}

		Set<String> uniSet = uniGram.keySet();
		List<String> uniList = new ArrayList<String>(uniSet);

		return uniList;
	}

	/**
	 * Put each pair of words (adjacent on the stack) in the word stack 
	 * that follow the non-stop-word and POS-structure on a stack
	 * 
	 * @param wordStack, a stack of strings generated by modifyWords
	 * @return biList, a List<String>
	 * @throws Exception
	 */
	public static List<String> biGram(Stack<String> wordStack) throws Exception {
		Hashtable<String, Integer> biGram = new Hashtable<String, Integer>();

		int size2 = wordStack.size();


		for (int i = 0; i < size2 - 1; i++) {
			String str1 = wordStack.get(i);
			String str2 = wordStack.get(i + 1);
			String finalStr = str1 + " " + str2;
			if (checkStopWords(str1) && checkStopWords(str2))
				if (!biGram.containsKey(finalStr))
					biGram.put(finalStr, 0);
		}

		Set<String> biSet = biGram.keySet();
		List<String> biList = new ArrayList<String>(biSet);

		return biList;
	}


	/**
	 * Put each three words in the word stack (group of three adjacent on the stack)
	 * that follow the non-stop-word and POS-structure on a stack
	 * 
	 * @param wordStack, a stack of strings generated by modifyWords
	 * @return triList, a List<String>
	 * @throws Exception
	 */
	public static List<String> triGram(Stack<String> wordStack) throws Exception {
		Hashtable<String, Integer> triGram = new Hashtable<String, Integer>();

		int size3 = wordStack.size();


		for (int i = 0; i < size3 - 2; i++) {
			String str1 = wordStack.get(i);
			String str2 = wordStack.get(i + 1);
			String str3 = wordStack.get(i + 2);
			String finalStr = str1 + " " + str2 + " " + str3;
			if (checkStopWords(str1) && checkStopWords(str3))
				if (!triGram.containsKey(finalStr))
					triGram.put(finalStr, 0);
		}

		Set<String> triSet = triGram.keySet();
		List<String> triList = new ArrayList<String>(triSet);

		return triList;
	}


	/**
	 * helper procedure to read all the contents of a file in a single string.
	 * 
	 * @param path
	 * @param encoding
	 * @return a string
	 * @throws IOException
	 */
	//reference: //reference: https://www.techiedelight.com/read-all-text-from-file-into-string-java/
	public static String readFile(String path, Charset encoding) throws IOException {

		byte[] encoded = Files.readAllBytes(Paths.get(path));

		return new String(encoded, encoding);
	}


	/**
	 * Calculate the precision score of our generated 5 words compare to the human selected words
	 * 
	 * @param filePath, a string
	 * @param top5, a List<String>
	 * @return precisionScore, a double
	 */
	public static double precisionScore (String filePath, List<String> top5) {

		double counter = 0.0;
		double precisionScore = 0.0;

		//create a hash table for all the key words selected by humans from the uncontrolled file.
		Hashtable<String, Integer> fileWords = new Hashtable<String, Integer>();
		Pattern pattern = Pattern.compile(";");

		//reference: https://www.techiedelight.com/read-all-text-from-file-into-string-java/
		//convert the contents of the file to a single string.
		String content = null;
		try {
			content = readFile(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}


		//reference: https://www.geeksforgeeks.org/remove-extra-spaces-string/
		//removes unnecessary spaces from string
		content = content.replaceAll("\\s+"," ").trim();

		//System.out.println(content);
		//System.out.println("\n\n");

		Matcher matcher = pattern.matcher(content);

		// Check all occurrences
		int position = 0;
		int len = content.length();


		//find the position of ";" and extract words without the ";"
		while (matcher.find()) {
			fileWords.put(content.substring(position, matcher.start()), 0);
			position = matcher.end() + 1;
		}

		fileWords.put(content.substring(position, len), 0);


		//convert hashkeys into an arraylist of strings.
		Set<String> test = fileWords.keySet();
		List<String> testList = new ArrayList<String>(test);


		System.out.println("The standard key words listed by humans are: "); 

		for (String x : testList) 
			System.out.print(x +"; ");

		System.out.println(); 
		System.out.println(); 


		//count the number of top candidates that are actually found in the gold standard.
		for(String name : top5) {
			if (fileWords.containsKey(name))
				counter++;
		}

		precisionScore = counter / 5;

		// System.out.println("The precision score is: " + precisionScore);

		//returns precision score
		return precisionScore;
	}



	// Separate testing for this class
	/**
	public static void main (String[] args) throws Exception{
		Stack<String> wordStack = modifyWords("/Users/fmy/Desktop/Sample.txt");


		List<String> uniList = uniGram(wordStack);
		List<String> biList = biGram(wordStack);
		List<String> triList = triGram(wordStack);

		System.out.println("Created uniList is"); 
		for (String x : uniList) 
		{
			if (checkPOS(x))
				System.out.println(x); 
		}


		System.out.println("*********************************");


		System.out.println("Created biList is"); 
		for (String x : biList)
		{
			if (checkPOS(x))
				System.out.println(x); 
		}

		System.out.println("*********************************");


		System.out.println("Created triList is"); 
		for (String x : triList) 
		{
			if (checkPOS(x))
				System.out.println(x); 
		}
	}

	 */
}


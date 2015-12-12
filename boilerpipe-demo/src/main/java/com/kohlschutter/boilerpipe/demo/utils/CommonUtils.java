package com.kohlschutter.boilerpipe.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
	public static String getStringMatchedForGivenPattern(String pattern,
			String targetData) {
		String dataToBeReturned = null;
		Pattern patternForGivenRegex = Pattern.compile(pattern);
		Matcher matcherForGivenRegex = patternForGivenRegex.matcher(targetData);
		if (matcherForGivenRegex.find()) {
			int begIndex = matcherForGivenRegex.start();
			int endIndex = matcherForGivenRegex.end();
			dataToBeReturned = targetData.substring(begIndex, endIndex);
		}
		return dataToBeReturned;
	}

	public static List<String> getMatchedStringsForGivenText(String pattern,
			String targetData) {
		List<String> allMatches = new ArrayList<String>();
		Pattern patternForGivenRegex = Pattern.compile(pattern);
		Matcher matcherForGivenRegex = patternForGivenRegex.matcher(targetData);
		while (matcherForGivenRegex.find()) {
			int begIndex = matcherForGivenRegex.start();
			int endIndex = matcherForGivenRegex.end();
			allMatches.add(targetData.substring(begIndex, endIndex));
		}
		return allMatches;
	}
	public static String textContentReadFromFile(String location)
	{
		File fileToBeRead =null;
		String dataread = null;
		BufferedReader br =null;
		StringBuilder sbr =new StringBuilder();
		if(location.startsWith("/"))
		{
			try{
			fileToBeRead = new File(location);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeRead)));
			
			while((dataread=br.readLine())!=null)
			{
				sbr.append(dataread);
				
			}
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			finally
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sbr.toString();
	}

}

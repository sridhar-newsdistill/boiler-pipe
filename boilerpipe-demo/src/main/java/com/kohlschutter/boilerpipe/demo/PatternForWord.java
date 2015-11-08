package com.kohlschutter.boilerpipe.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternForWord {
	public static void main(String args[])
	{
		String matcher="llllllllllllllllllll*********";
		String regex="[\\p{L}\\p{Nd}\\p{Nl}\\p{No}]";
	Pattern regexPatForWord=Pattern.compile(regex);
	Matcher match=null;
	match=regexPatForWord.matcher(matcher);
	if(match.find())
	{
		int startIndex= match.start();
		int endIndex=match.end();
		System.out.println(matcher.substring(startIndex, endIndex));
	}
	
	}

}

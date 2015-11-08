package com.kohlschutter.boilerpipe.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTest {
public static void main(String args[])
{
	String htmlText="<html><h1></html>";
	String regExForStartOfTheTag="<[^>/]*>";
	String regExForEndOfTheTag="</[^>]*>";
	Pattern patternForStartTag=Pattern.compile(regExForStartOfTheTag);
	Pattern patternForEndTag=Pattern.compile(regExForEndOfTheTag);
	Matcher	matcherForEnd=patternForEndTag.matcher(htmlText);
	Matcher matcherFor=patternForStartTag.matcher(htmlText);
	while(matcherFor.find())
	{
	 int begIndex=	matcherFor.start();
	  int endIndex= matcherFor.end();
		System.out.println(htmlText.substring(begIndex, endIndex));
	}
	if(matcherForEnd.find())
	{
		  int begIndex=	matcherForEnd.start();
		  int endIndex= matcherForEnd.end();
		  System.out.println(htmlText.substring(begIndex, endIndex));
	}
	
}
}

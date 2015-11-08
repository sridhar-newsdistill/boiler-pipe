package com.kohlschutter.boilerpipe.demo;

import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class TagBalanceHtml {

	public static void main(String args[]) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		SAXParser parser=new SAXParser();
		//DOMFragmentParser parser=new DOMFragmentParser();
		parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
		
		//parser.parse();
		
	}
}

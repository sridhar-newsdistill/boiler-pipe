package com.newsdistill.articleextractor;

import java.io.IOException;

public class Driver {
	
	static String regExForAnchorTagRemoval = "(?i)((also)?(related|read|similar)[\\s]*(also|more)?[\\W]*<a[^>]*>[^<]*(<span[^>]*[^<]*<\\/span>)?<[\\s]*\\/a>)";
	final static String REGEX_FOR_ALL_START_HEADINGS = "<(?i)(H[1-5])[^>]*>";
	final static String REGEX_FOR_ALL_END_HEADINGS = "<[/](?i)([//s]*H[1-5])[^>]*>";

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
        System.out.println("printinghit");
		//String data = null;// Utils.getHtmlAsString("http://www.sakshieducation.com/EnglishStory.aspx?cid=2&sid=115&nid=117796");
		BaseArticleExractor article = new ContentExtractor(
				"http://www.thestatesman.com/news/india/politics-happened-in-dalit-student-s-death-punia/117448.html");

		ArticleContent ac = article.getTotalContent(3,"Asia/Calcutta");
		if(ac != null){
		System.out.println(ac.getArticleDate());
		System.out.println(ac.getTitle());
	System.out.println(ac.getDescription().replaceAll(regExForAnchorTagRemoval, ""));
		
		System.out.println();
	    System.out.println(ac.getImageUrl());
		}
		// System.out.println();
        //System.out.println(doc.toString());
		// System.out.println(data);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println((end - start) / 1000);
	}

}

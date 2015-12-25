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
				"http://www.cinejosh.com/news-in-telugu/2/27739/bhadram-be-careful-brotheruu-bhadram-be-careful-brother-nikhil-maruthi-bhadram-be-careful-brotheruu-trailer-launch.html");

		ArticleContent ac = article.getTotalContent();
		if(ac != null){
		System.out.println(ac.getArticleDate());
		System.out.println(ac.getTitle());
	System.out.println(ac.getDescription().replaceAll(regExForAnchorTagRemoval, ""));
		
		
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

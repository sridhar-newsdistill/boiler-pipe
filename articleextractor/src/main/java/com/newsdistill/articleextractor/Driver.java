package com.newsdistill.articleextractor;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Driver {
	static String regExForAnchorTagRemoval = "(?i)(also)?(related|read|similar)[\\s]*(also|more)?[\\W]*<a[^>]> [^<]*(<span[^>]* [^<]*</span>)?(</a>)?";
	final static String REGEX_FOR_ALL_START_HEADINGS = "<(?i)(H[1-5])[^>]*>";
	final static String REGEX_FOR_ALL_END_HEADINGS = "<[/](?i)([//s]*H[1-5])[^>]*>";

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();

		String data = null;// Utils.getHtmlAsString("http://www.sakshieducation.com/EnglishStory.aspx?cid=2&sid=115&nid=117796");
		BaseArticleExractor article = new ContentExtractor(
				"http://www.hindustantimes.com/tech/for-the-first-time-ever-4g-smartphones-overtake-3g-smartphones/story-nUtfSg1j4pVJPRYWHvdGmI.html");

		ArticleContent ac = article.getTotalContent(3, "IST");
		if(ac != null){
		System.out.println(ac.getArticleDate());
		System.out.println(ac.getDescription());
		
		/*doc.select("img ~ span").remove();
		doc.select("img + span").remove();
		doc.select("img").empty();*/
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

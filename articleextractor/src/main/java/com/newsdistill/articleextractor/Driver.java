package com.newsdistill.articleextractor;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver {
	static String regExForAnchorTagRemoval = "(?i)(also)?(related|read|similar)[\\s]*(also|more)?[\\W]*<a[^>]> [^<]*(<span[^>]* [^<]*</span>)?(</a>)?";
	final static String REGEX_FOR_ALL_START_HEADINGS = "<(?i)(H[1-5])[^>]*>";
	final static String REGEX_FOR_ALL_END_HEADINGS = "<[/](?i)([//s]*H[1-5])[^>]*>";

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		/*
		 * Map<String, Object> data = HTMLFetcherUtil .getBytesFromURL(new URL(
		 * "http://www.thehindu.com/news/national/india-first-only-religion-of-government-and-constitution-its-only-scripture-says-modi/article7923917.ece?homepage=true"
		 * ));
		 */BaseArticleExractor article = new ContentExtractor(
				"http://www.livemint.com/Politics/zA1uOWlVK6hjDt5BAmLO0H/Colorado-shooting-3-killed-9-injured-in-attack-on-family-p.html");
		/*
		 * //String refineddesc=article.getDescription();
		 * 
		 * byte urlBytes[] = (byte[])data.get("bytes");
		 * System.out.println(article.getDate(urlBytes.toString()));
		 */
		ArticleContent ac = article.getTotalContent(4);
		System.out.println(ac.getArticleDate());
		System.out.println(ac.getImageUrl());

		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println((end - start) / 1000);
	}

}

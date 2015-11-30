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
		
	BaseArticleExractor article = new ContentExtractor(
				"http://www.apherald.com/Politics/ViewArticle/105615/ys-jagan-chandrababu-media-opposition-same-dialogu/");
		
		ArticleContent ac = article.getTotalContent(3,"IST");
		System.out.println(ac.getArticleDate());
		System.out.println(ac.getImageUrl());
		System.out.println(ac.getDescription());

		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println((end - start) / 1000);
	}

}

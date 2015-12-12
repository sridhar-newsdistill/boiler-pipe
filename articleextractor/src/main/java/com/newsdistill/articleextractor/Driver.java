package com.newsdistill.articleextractor;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newsdistill.articleextractor.utils.Utils;

public class Driver {
	static String regExForAnchorTagRemoval = "(?i)(also)?(related|read|similar)[\\s]*(also|more)?[\\W]*<a[^>]> [^<]*(<span[^>]* [^<]*</span>)?(</a>)?";
	final static String REGEX_FOR_ALL_START_HEADINGS = "<(?i)(H[1-5])[^>]*>";
	final static String REGEX_FOR_ALL_END_HEADINGS = "<[/](?i)([//s]*H[1-5])[^>]*>";

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		
	String data =	null;//Utils.getHtmlAsString("http://www.sakshieducation.com/EnglishStory.aspx?cid=2&sid=115&nid=117796");
	BaseArticleExractor article = new ContentExtractor(
				"http://movies.ndtv.com/bollywood/shah-rukh-explains-how-he-is-an-accidental-movie-star-to-iim-bangalore-1254076?pfrom=home-topstory");
		
		ArticleContent ac = article.getTotalContent(3,"IST");
		System.out.println(ac.getArticleDate());
		
		System.out.println(ac.getImageUrl());
		System.out.println(ac.getDescription());

	    //System.out.println(data);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println((end - start) / 1000);
	}

}

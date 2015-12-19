package com.newsdistill.articleextractor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	public static int getWordCount(String str) {
		if (StringUtils.isBlank(str)) {
			return 0;
		}
		return str.split("\\s+").length;
	}
	public static String getMathchedText(Pattern pattern,String targetString)
	{
		String matchedString ="";
		Matcher matherForText = pattern.matcher(targetString);
		if(matherForText.find())
		{
		int startIndex =	matherForText.start();
		int endIndex  = matherForText.end();
		matchedString = targetString.substring(startIndex, endIndex);
		}
		return matchedString;
	}
	public static String getHtmlAsString(String url) {
		StringBuilder sb = new StringBuilder(url);
		InputStream ins = null;
		String data = null;
		try {
			URL pagelink = new URL(url);
			URLConnection urlconn = pagelink.openConnection();
			ins = urlconn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));

			while ((data = br.readLine()) != null) {
				sb.append(data);
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				ins.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public static String getAbsoluteUrl(String url, String imageUrl) {
		if (StringUtils.isBlank(imageUrl) || imageUrl.trim().equals("/")) {
			return null;
		}
		URI uri_absolute = null;
		;
		URI uri_relative = null;
		try {
			uri_absolute = new URI(url.replace(" ", "%20"));
			uri_relative = new URI(imageUrl.replace(" ", "%20"));
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}

		URI uri_absolute_result = uri_absolute.resolve(uri_relative);
		return uri_absolute_result.toString();
	}
	public static List<String> getMatchedStringsForGivenText(Pattern pattern,
			String targetData) {
		List<String> allMatches = new ArrayList<String>();
		//Pattern patternForGivenRegex = Pattern.compile(pattern);
		Matcher matcherForGivenRegex = pattern.matcher(targetData);
		while (matcherForGivenRegex.find()) {
			int begIndex = matcherForGivenRegex.start();
			int endIndex = matcherForGivenRegex.end();
			allMatches.add(targetData.substring(begIndex, endIndex));
		}
		return allMatches;
	}
	

	
}

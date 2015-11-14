package com.kohlschutter.boilerpipe.demo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UrlExtractor {

	public static Set<String> getUrlFromtTheFeedUrl(String feedUrl,
			String pattern, String hostName) {

		// pattern = "s:(#ContentPlaceHolder1_dlstPoliticsTab a:[href])";
		Document doc = Jsoup.parse(feedUrl);
		Elements elems = doc.getAllElements();
		String[] arr = pattern.split("&&");
		System.out.println("working fine");
		System.out.println(arr.length);
		if (arr.length <= 1) {
			if (arr.length == 1) {
				Set<String> elemts = selectMatchedElements(elems, pattern,
						hostName);
				for (String url : elemts) {
					System.out.println(url);
				}
			} else {
				return null;
			}
		}

		return null;
	}

	public static Set<String> selectMatchedElements(Elements elems,
			String selectionCriteria, String hostname) {
		Set<String> urlsIdentified = new LinkedHashSet<String>();
		if (selectionCriteria.startsWith("s:")) {
			if (selectionCriteria.length() > 3) {
				String filterQuery = selectionCriteria.substring(3,
						selectionCriteria.length() - 1);
				String selector[] = filterQuery.split("\\s");
				int index = 0;
				while (index < selector.length - 1) {
					elems = elems.select(selector[index]);
					if (elems.isEmpty()) {
						return null;
					}
					index++;
				}
				String tagNAttribute = selector[selector.length - 1];
				if (tagNAttribute.contains(":")) {
					String tagCorrespondingAttr[] = tagNAttribute.split(":");
					elems = elems.select(tagCorrespondingAttr[0]);
					tagCorrespondingAttr[1] = tagCorrespondingAttr[1]
							.replaceAll("[^a-zA-Z]", "");
					for (Element tag : elems) {
						String url = tag.attr(tagCorrespondingAttr[1]);
						if (url.length() < 5 || !url.startsWith("http://")) {

							url = hostname + "/" + url;
						}
						if (url.contains(hostname)) {
							urlsIdentified.add(url);
						}
					}
				}
				// /Elements selected = elems.select(filterQuery);
				// System.out.println(elems.select(filterQuery).toString());
			}

		}
		return urlsIdentified;
	}

	public static Elements removeUnnecessaryClasses(Elements elements) {
        
		return elements;
	}

	public static void main(String args[]) throws IOException {
		String feedUrl = "http://www.patrika.com/business/market/";
		URL urlinfo = new URL(feedUrl);
		String hostname = urlinfo.getHost();

		String content = getContentFromUrl(feedUrl);

		String pattern = "s:(.education a:[href])";
		getUrlFromtTheFeedUrl(content, pattern, hostname);
	}

	public static String getContentFromUrl(String url) throws IOException {
		InputStream ins = null;
		StringBuilder sbr = new StringBuilder();
		String data = null;
		URL pageurl = new URL(url);
		URLConnection urlc = pageurl.openConnection();
		ins = urlc.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
		while ((data = br.readLine()) != null) {
			sbr.append(data);
		}
		//System.out.println(sbr.toString());
		return sbr.toString();
	}

}

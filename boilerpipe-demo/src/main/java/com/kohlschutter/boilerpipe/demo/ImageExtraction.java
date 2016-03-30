package com.kohlschutter.boilerpipe.demo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImageExtraction {

	public static void main(String[] args) throws IOException {

		long start = System.currentTimeMillis();
		Document doc = null;
		BufferedImage image=null;
		String url = "http://www.livehindustan.com/news/national/article1-ruckus-in-lok-sabha-continued-on-second-day-over-birendra-singh-derogatory-remark-507720.html";
		HttpURLConnection httpcon = null;
		URL imageResourceUrl = null;
		URL pageUrl = new URL(url);
		int maxsize = 0;
		String maxSizeImage = null;
		System.out.println(pageUrl.getHost());
		Connection connection = Jsoup.connect(url);
		doc = connection.get();
		// pageUrl.
		doc.select("a").remove();

		doc.select("script").remove();
      
		doc.select("noscript").remove();
		Set<String> imageUrls = new LinkedHashSet<String>();
		Elements elems = doc.select("img");
		for (Element element : elems) {
			String imageUrl = element.attr("src");
			if (!StringUtils.isEmpty(imageUrl)) {
				if (!imageUrl.startsWith("http")) {
					imageUrl = getAbsoluteUrl(url, imageUrl);
				}
				imageUrls.add(imageUrl);
				
			} else {
				String imageData = element.toString();
			}
		}
		for(String imgUrl : imageUrls){
			imageResourceUrl = new URL(imgUrl);
			httpcon = (HttpURLConnection) imageResourceUrl.openConnection();
			image = ImageIO.read(imageResourceUrl);
			int size =	image.getHeight()*image.getWidth();
			// = httpcon.getContentLength();
			System.out.println(size+" "+imgUrl);
			if(size > maxsize)
			{
				maxsize = size;
				//System.out.println(maxsize);
				maxSizeImage = imgUrl;
			}
			}
		long end = System.currentTimeMillis();
		System.out.println((end-start)/1000);
		System.out.println(maxSizeImage);
		
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

}

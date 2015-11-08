package com.newsdistill.articleextractor;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Driver {

	public static void main(String[] args) throws IOException {
		long start=System.currentTimeMillis();
		BaseArticleExractor article=new ContentExtractor("http://hindi.webdunia.com/bollywood-movie-review/main-aur-charles-randeep-hooda-richa-chaddha-fil-review-samay-tamrakar-115103000041_1.html");
	
		article.getTotoalContent("http://hindi.webdunia.com/bollywood-movie-review/main-aur-charles-randeep-hooda-richa-chaddha-fil-review-samay-tamrakar-115103000041_1.html");
/*Connection con=	Jsoup.connect("http://www.apherald.com/Politics/ViewArticle/102878/father-rented-cycles-son-is-the-minister/");
	Document doc=	con.get();
	System.out.println(doc.text());*/
		long end=System.currentTimeMillis();
		System.out.println(end-start);
		System.out.println((end-start)/1000);
	}

}

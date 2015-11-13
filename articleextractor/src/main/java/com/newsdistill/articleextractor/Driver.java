package com.newsdistill.articleextractor;

import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws IOException {
		long start=System.currentTimeMillis();
		BaseArticleExractor article=new ContentExtractor("http://www.patrika.com/news/political/amit-shah-bhagwat-statement-cuse-bjp-defeat-in-bihar-election-jeetamram-manjhi-1131856/");
	
		ArticleContent ac=article.getTotoalContent();
		System.out.println(ac.getDescription());
        //System.out.println(ac.toString()); 
		long end=System.currentTimeMillis();
		System.out.println(end-start);
		System.out.println((end-start)/1000);
	}

}

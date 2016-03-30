package com.newsdistill.articleextractor;

import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws IOException {
		long start=System.currentTimeMillis();
		//comment added
		BaseArticleExractor article=new ContentExtractor("http://indianexpress.com/article/sports/cricket/ranji-trophy-2015-tamil-nadu-trigger-railways-collapse-to-win-by-eight-wickets/");
	
		ArticleContent ac=article.getTotoalContent();
		System.out.println(ac.getDescription());
        //System.out.println(ac.toString()); 
		long end=System.currentTimeMillis();
		System.out.println(end-start);
		System.out.println((end-start)/1000);
	}

}

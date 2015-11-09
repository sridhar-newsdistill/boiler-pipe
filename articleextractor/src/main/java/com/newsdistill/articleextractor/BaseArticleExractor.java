package com.newsdistill.articleextractor;

public interface BaseArticleExractor {
	public ArticleContent getTotoalContent();
    public String getTitle(String url);
    public String getDescription(String url);
    public String getDate(String content);
    public String getImage(String conent);
    public String getLogo();
    public String getDomain();
    
}

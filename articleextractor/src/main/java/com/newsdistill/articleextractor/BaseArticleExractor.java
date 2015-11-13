package com.newsdistill.articleextractor;

import java.net.URL;
import java.util.Date;

public interface BaseArticleExractor {
	public ArticleContent getTotoalContent();
    public String getTitle(String url);
    public String getDescription(URL url);
    public Date getDate(String content);
    public String getImage(String conent);
    public String getLogo();
    public String getDomain();
    
}

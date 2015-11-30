package com.newsdistill.articleextractor;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

public interface BaseArticleExractor {
	public ArticleContent getTotoalContent();
	public ArticleContent getTotalContent(int imageLookupCode,String zoneInfo);
    public String getTitle(String url);
    public String getDescription(URL url);
    public String getDescription(URL url,byte[] content);
    public String getImage(byte[] htmlInBytes, Charset cs);
    public Date getDate(String content,String zone);
    public String getImage(String conent);
    public String getLogo();
    public String getDomain();
    
}

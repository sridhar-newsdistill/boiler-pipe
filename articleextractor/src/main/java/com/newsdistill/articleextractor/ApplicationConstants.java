package com.newsdistill.articleextractor;

import java.util.regex.Pattern;


public class ApplicationConstants {
  public static final String titleTags = "h1|h2|title|h3|h4|.heading|#heading|meta[property=\"og:title\"]";
  public static final String encodingForLineBreaks = "1922135";
  public static final String encodingForImageTags = "ndImageEncodedImageRetention531221920152020";

  public static final String TAG_NAME_TAGNUM_DELIM = "##";
  public static final String TAG_CONTENT_WORDCNT_DELIM = ":::";
  public static final String REGEX_TO_FIND_CLEAN_TG_NAME_FROM_ATTRIBUTE_ENABLD_TAG = "<[\\s]*[a-zA-Z0-9]+";
  public static final String noTagEncoding = "<5312219>";
  public static final String regExToFindStartOfTheString = "^<[^/][^>]*>";
  public static final String regExForEndOfTheTag = "^</[^>]*>";
  public static final String delimiterBeforeOpenAndClosingTags = "<";
  public static final String regexForImageExtraction = "<[\\s]*(IMG|img)[^>]*>";
  public static final String REGEX_FOR_EMPTY_TAG_REMOVAL = "<[^/][^>]*>[\\s]*<[/][^>]*>";
  public static final Pattern patternForStartTag = Pattern.compile(regExToFindStartOfTheString);
  public static final Pattern patternForFindingExactTagName = Pattern
      .compile(REGEX_TO_FIND_CLEAN_TG_NAME_FROM_ATTRIBUTE_ENABLD_TAG);
  public static final Pattern patternForImageExtraction = Pattern.compile(regexForImageExtraction);
  public static final Pattern patternForEndTag = Pattern.compile(regExForEndOfTheTag);
  public static final Pattern patternForBeginningHtmlElement = Pattern.compile(delimiterBeforeOpenAndClosingTags);
  public static final String regExForimgExtraction = "<(h([1-5]+)|title)";
  public static final Pattern patternForTitle = Pattern.compile(regExForimgExtraction);
  // in case if we don't find title in these headers we will directly download
  // from url
  public static final String regexForTitleExtraction = "<(h1|h2|h3|title)[^>]*>";
  public static final Pattern patternForTitleExtraction = Pattern.compile(regexForTitleExtraction,
      Pattern.CASE_INSENSITIVE);
  public static final String dateWhileListTags = ".(\\*date\\*|pub\\*|\\*info\\*|\\*time\\*|\\*calendar\\*)";
  public static final String dateMetaTagInfo = "meta[property='((?i)(\\*date\\*|\\*Modified*))']";
  public static final String regExofClassesForDateIdentificaion =
      "[class~=(?i)(.*Pub.*|.*date.*|.*info.*|.*time.*|.*calendar.*|.*post.*)]";
  public static final String REGEX_FOR_HEADINGTAGS = "<(?i)H[1-5][^>]*>[^<]*<[/](?i)H[1-5][^>]*>";
  public static String DESCRIPTION_HTML_TAGS_WHITELIST = "p,br,h1,h2,h3,h4,h5,h6,font";
  //genreally image lies within src attibute in case if its not available in src then we are looking for attibutes starts with data-* 
  public static String REG_FOR_IMGEDATA_ATTR = "(?i)(src|data[^=]*)";
  public static String REG_EX_FOR_IMAGEATTR_EXTRACTION =
      "(?i)(src[\\s]*|data-[^=]*)=[\\s]*[\'\"].*[\\/].*[^\"\']*[\"\']";
  public static Pattern PATTERN_FOR_ATTR_DATA = Pattern.compile(REG_EX_FOR_IMAGEATTR_EXTRACTION);
  public static final String USER_AGENT = "User-Agent";
  public static final String USER_AGENT_VALUES =
      "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
  public static final int MINIMUM_HEIGHT_OF_IMAGE = 150;
  public static final String REG_EX_FOR_RELATED_ARTICLES =
      "(?i)((also)?(related|read|similar)[\\s]*(also|more)?[\\W]*<a[^>]*>[^<]*(<span[^>]*[^<]*<\\/span>)?<[\\s]*\\/a>)";
  public static String EXCLUDE_RECOMMENDED_ARTICLES_FROM_DESCRIPTION =
      "read more|read also|also read|more like this|For more news|for the latest|for latest|Click here to download|see also|copy right";

}

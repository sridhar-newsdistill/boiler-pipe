package com.newsdistill.articleextractor;

import static com.newsdistill.articleextractor.ApplicationConstants.patternForEndTag;
import static com.newsdistill.articleextractor.ApplicationConstants.titleTags;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.newsdistill.articleextractor.utils.Utils;

public class ContentExtractor implements BaseArticleExractor {

	Logger log = Logger.getLogger(ContentExtractor.class.getName());
	StringTokenizer titleTokens = new StringTokenizer(titleTags, "|");
	final static String REGEX_FOR_ATTRIBUTECONTENT = "=[\'\"]";
	final static Pattern PAT_FOR_TEXT_IN_ATTR = Pattern.compile(REGEX_FOR_ATTRIBUTECONTENT);
	private ArticleContent contentIdentified = new ArticleContent();
	private String Url = null;
	private Charset cs = null;
	private int channelId = 0;
	private String mainImageUrl = null;

	// private Map<String, String> imagesInfoMap = null;

	public ContentExtractor() {
		cs = Charset.forName("UTF-8");
	}

	public ContentExtractor(String url) {
		this.Url = url;
		cs = Charset.forName("UTF-8");
	}

	public ContentExtractor(String url, int channelId) {
		this.Url = url;
		cs = Charset.forName("UTF-8");
		this.channelId = channelId;
	}

	@Override
	public ArticleContent getTotalContent() {
		return getTotalContent("Asia/Kolkata");
	}

	@Override
	public ArticleContent getTotalContent(String zone) {
		// URL pagelink = null;
		byte[] htmlBytes = null;
		try {
			Map<String, Object> resultMap = HTMLFetcherUtil.getBytesFromURL(new URL(this.Url));
			htmlBytes = (byte[]) resultMap.get("bytes");
			if (htmlBytes == null) {
				return null;
			}

			this.cs = (Charset) resultMap.get("charset");
			if (this.cs == null) {
				this.cs = Charset.forName("UTF-8");
			}
		} catch (MalformedURLException e1) {
			this.log.info("error messaage " + e1.getMessage() + " Cause " + e1.getCause() + " stack trace" + e1.getStackTrace());
			e1.printStackTrace();
		} catch (@SuppressWarnings("hiding") IOException e1) {
			this.log.info("error messaage " + e1.getMessage() + " Cause " + e1.getCause() + " stack trace" + e1.getStackTrace());
			e1.printStackTrace();
		}

		String contentAvailableFrom = new String(htmlBytes);
		contentIdentified.setUrl(this.Url);
		contentIdentified.setDomain(getDomain());
		contentIdentified.setImageUrl(getImage(contentAvailableFrom));
		// contentIdentified.setTitle(getTitle(contentAvailableFrom));
		try {
			String description = getDescription(new URL(this.Url), htmlBytes);
			Document descriptionDocument = Jsoup.parse(description);
			descriptionDocument.select("li a").remove();
			//TO-DO test it thoroughly
			Elements probableRelatedElements=descriptionDocument.select("a");
			for (Element element : probableRelatedElements) {
			  if(isInValidAnchorElement(element)){
			    if(element.parent()!=null){
			      element.remove();
			    }
			  }
      }
			contentIdentified.setDescription(descriptionDocument.toString());
			contentIdentified.setArticleDate(getDate(contentAvailableFrom, zone));
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IndexOutOfBoundsException arrayIndexRange) {
			arrayIndexRange.printStackTrace();
		}
		return contentIdentified;
	}

	@Override
	public ArticleContent getTotalContent(int imageLookupCode, String zone) {

		byte[] htmlBytes = null;
		Elements imageElmentsInDescription = null;
		try {
			Map<String, Object> resultMap = HTMLFetcherUtil.getBytesFromURL(new URL(this.Url));
			htmlBytes = (byte[]) resultMap.get("bytes");
			if (htmlBytes == null) {
				return null;
			}
			this.cs = (Charset) resultMap.get("charset");
			if (this.cs == null) {
				this.cs = Charset.forName("UTF-8");
			}

		} catch (MalformedURLException e1) {

			e1.printStackTrace();
		} catch (@SuppressWarnings("hiding") IOException e1) {

			e1.printStackTrace();
		}
		String htmlPageContent = new String(htmlBytes);

		contentIdentified.setUrl(this.Url);
		contentIdentified.setDomain(getDomain());
		// contentIdentified.setTitle(getTitle(htmlPageContent));
		contentIdentified.setArticleDate(getDate(htmlPageContent, zone));

		try {
			String description = getDescription(new URL(this.Url), htmlBytes);
			Document descriptionDocument = Jsoup.parse(description);
			imageElmentsInDescription = descriptionDocument.select("img").clone();
			descriptionDocument.select("li a").remove();

			contentIdentified.setDescription(descriptionDocument.toString());
			contentIdentified.setImageUrl(getImage(imageElmentsInDescription));
			contentIdentified.setArticleDate(getDate(htmlPageContent, zone));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException arrayIndexRange) {
			arrayIndexRange.printStackTrace();
		}
		// images are dependent on description

		return contentIdentified;
	}

	@Override
	public String getTitle(String content) {

		Document htmldoc = null;
		String doucmentTitle = null;

		htmldoc = Jsoup.parse(content);

		if (htmldoc == null) {
			return null;
		}
		while (titleTokens.hasMoreElements() && doucmentTitle == null) {
			String value = titleTokens.nextElement().toString();
			if(this.channelId == 401)
			{
				value="h3";
				this.log.info("inside loop: channel Id for Saamana: " + this.channelId +"  value:"+value);
			}
			Elements elements = htmldoc.select(value);
			if (elements == null || elements.first() == null) {
				continue;
			} else {
				for (Element element : elements) {
					if (!StringUtils.isBlank(element.text()) && element.text().length() >= 10) {
						doucmentTitle = element.html();
						if (doucmentTitle == null) {
							continue;
						}
						break;
					} else {
						continue;
					}
				}
				break;
			}

		}
		return doucmentTitle;

	}

	public String getDescription(URL url, int imageLookupCode) {
		Document doc = null;
		String resultDescFromBoilerPipe = null;
		Elements elems = null;
		if (imageLookupCode != 3) {
			return getDescription(url);
		}
		resultDescFromBoilerPipe = getDescription(url);
		doc = Jsoup.parse(resultDescFromBoilerPipe);
		if (doc != null) {
			elems = doc.select("img");
			if (elems != null) {
				mainImageUrl = getImage(elems);
				return doc.toString();
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	public String getDescription(URL url) {
		byte[] htmlBytes = null;
		String resultFromBoilerPipe = "";
		try {
			if (StringUtils.isBlank(this.Url)) {
				this.Url = url.toString();
			}
			Map<String, Object> resultMap = HTMLFetcherUtil.getBytesFromURL(new URL(this.Url));
			if (resultMap != null) {
				htmlBytes = (byte[]) resultMap.get("bytes");
				this.cs = (Charset) resultMap.get("charset");
				if (this.cs == null) {
					this.cs = Charset.forName("UTF-8");
				}
				resultFromBoilerPipe = getDescription(new URL(this.Url), htmlBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultFromBoilerPipe;
	}

	public String getDescription(URL url, byte[] contentInBytes) {
		ArticleExtractor ce = null;
		if (contentInBytes == null) {
			return null;
		}
		ce = CommonExtractors.ARTICLE_EXTRACTOR;
		// String classForRemoval = "nd_boiler_pipe_tag";
		final HTMLHighlighter contentHighlighter = HTMLHighlighter.newHighlightingInstance();
		String htmlDocument = null;

		try {
			htmlDocument = new String(contentInBytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Document doc = Jsoup.parse(htmlDocument);

		Map<String, String> imageUrlKeyVlaueMap = new LinkedHashMap<String, String>();

		Elements elements2 = doc.select(":matchesOwn((?i)(" + ApplicationConstants.EXCLUDE_RECOMMENDED_ARTICLES_FROM_DESCRIPTION + "))");
		for (org.jsoup.nodes.Element e : elements2) {

			if (e != null) {
				org.jsoup.nodes.Element parent = e.parent();

				if (parent != null) {
					String parentText = parent.text();
					int parentLength = parentText.length();

					if (parentLength <= 300) {
						parent.remove();
					} else if (e.text().length() <= 300) {
						e.remove();
					}
				} else if (e.text().length() <= 300) {
					e.remove();
				}
			}
		}

		String content = getEncodedImageurlUrlContent(doc, imageUrlKeyVlaueMap);
		// System.out.println(doc.toString());
		contentInBytes = content.getBytes();
		String resultFromBoilerPipe = "";

		try {
			resultFromBoilerPipe = contentHighlighter.process(ce, contentInBytes, this.cs, this.channelId);

			Set<String> imageSet = imageUrlKeyVlaueMap.keySet();
			for (String object : imageSet) {
				resultFromBoilerPipe = resultFromBoilerPipe.replace(object, imageUrlKeyVlaueMap.get(object));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException Iob) {
			Iob.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}

		return resultFromBoilerPipe;
	}

	@Override
	public Date getDate(String content, String zone) {

		Document doc = Jsoup.parse(content);

		return getFinalDate(doc, zone);
	}

	public String getImage(byte[] htmlInBytes, Charset cs) {
		String documentString = null;
		Set<String> imageUrls = null;
		Document htmlDoc = null;
		documentString = new String(htmlInBytes);
		if (!StringUtils.isEmpty(documentString)) {
			htmlDoc = Jsoup.parse(documentString);

			htmlDoc.select("li a").remove();
			htmlDoc.select("marquee").remove();
			htmlDoc.select("script").remove();
			htmlDoc.select("input").remove();
			htmlDoc.select("noscript").remove();
			Elements elems = htmlDoc.select("img");
			//System.out.println(elems);
			imageUrls = new LinkedHashSet<String>();
			for (Element element : elems) {
				String imageUrl = null;
				// if (StringUtils.isEmpty(imageUrl)) {
				String imageData = element.toString();
				List<String> imgAttrs = Utils.getMatchedStringsForGivenText(ApplicationConstants.PATTERN_FOR_ATTR_DATA, imageData);

				for (String imageAttribute : imgAttrs) {

					Matcher matcherForTextInAttr = PAT_FOR_TEXT_IN_ATTR.matcher(imageAttribute);
					int startIndx = 0;
					int endIndx = imageAttribute.length() - 1;
					if (matcherForTextInAttr.find()) {
						startIndx = matcherForTextInAttr.start();
						// endIndx = matcherForTextInAttr.end();
					}
					if (startIndx == endIndx) {
						return null;
					}
					imageUrl = imageAttribute.substring(startIndx + 2, endIndx);
					if (!imageUrl.startsWith("http")) {
						imageUrl = Utils.getAbsoluteUrl(this.Url, imageUrl);
					}
					if (imageUrl != null)
						imageUrls.add(imageUrl);
				}

			}
			return getMainImage(imageUrls);
		} else {
			return null;
		}
	}

	@Override
	public String getImage(String conent) {

		Document doc = null;
		Elements nodes = null;
		String imageUrl = null;
		doc = Jsoup.parse(conent);
		String imagePattern = "meta[property=og:image]|img[src]|img[data*]";
		StringTokenizer st = new StringTokenizer(imagePattern, "|");
		doc.select("meta[property=og:image]");
		while (st.hasMoreElements()) {

			nodes = doc.select(st.nextElement().toString());

			if (nodes == null) {
				System.out.println("caught up here");
				continue;
			}

			Element imageTag = nodes.first();

			if (imageTag != null) {
				if (imageTag.tagName().equalsIgnoreCase("meta")) {
					imageUrl = imageTag.attr("content");

				}
				break;
			}

		}
		return imageUrl;

	}

	@Override
	public String getLogo() {

		return null;
	}

	@Override
	public String getDomain() {
		String domain = null;
		try {
			URL articleUrl = new URL(Url);
			domain = articleUrl.getHost();
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}

		return domain;
	}

	public static boolean isEndofTheTag(String remainingString) {
		if (!StringUtils.isEmpty(remainingString)) {
			Matcher mathcerForEnd = patternForEndTag.matcher(remainingString);
			boolean truthval = mathcerForEnd.find();

			return truthval;
		} else {
			return false;
		}

	}

	private Date getFinalDate(Document doc, String zone) {
		Elements elems = null;
		int dateindex = 0;
		Map<String, Integer> mapForDate = new LinkedHashMap<String, Integer>();
		// Pattern pattern =
		// Pattern.compile(DateExtractor.regexForSelectiontags);

		doc.getElementsMatchingOwnText("©[\\s]+" + Calendar.getInstance().get(Calendar.YEAR) + "").remove();
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		elems = doc.getAllElements();
		Elements allSelectedElements = elems.clone();
		allSelectedElements = allSelectedElements.select(DateExtractor.regexForSelectiontags);
		if (allSelectedElements != null && allSelectedElements.size() > 0) {
			for (Element e : allSelectedElements) {

				if (e.text().toLowerCase().contains(year.toString()) && e.text().length() < 100) {
					dateCountMap(mapForDate, e.text());

				}
			}
		}
		if (elems != null && elems.size() > 0) {
			for (Element block : elems) {
				Attributes node = block.attributes();
				Iterator<Attribute> it = node.iterator();
				while (it.hasNext()) {
					Attribute attr = it.next();
					String val = attr.getValue();
					val = val.toLowerCase();
					Matcher matcher = DateExtractor.patternval.matcher(val);

					if ((matcher.find() || !StringUtils.isEmpty(val)) && (val.toLowerCase().contains("date") || val.toLowerCase().contains("modified"))) {
						String probableDate = block.attr("content");
						if (!StringUtils.isEmpty(probableDate) && (probableDate.toLowerCase().contains(year.toString()) && probableDate.length() < 100)) {

							dateCountMap(mapForDate, probableDate);
							// break;
						}
					}

				}
			}
		}
		// Integer year = Calendar.getInstance().get(Calendar.YEAR);
		if (mapForDate.size() <= 1) {
			elems.select("a,meta,script").remove();
			allSelectedElements = elems.clone();
			for (Element element : allSelectedElements) {

				String textFromElement = element.text();
				int textBlockLength = element.text().length();
				if (textBlockLength > 10 && textBlockLength < 50) {
					if (!StringUtils.isEmpty(textFromElement) && (textFromElement.toLowerCase().contains(year.toString()) && textFromElement.length() < 100)) {
						// System.out.println(textFromElement);
						dateCountMap(mapForDate, textFromElement);
						// break;
					}
				}
			}

		}

		List<Date> datesidentified = new ArrayList<Date>();
		Set<String> dates = mapForDate.keySet();
		if (dates.size() <= 0) {

			Calendar calandar = Calendar.getInstance();
			calandar.add(Calendar.HOUR, -2);

			return calandar.getTime();
		} else if (dates.size() == 1) {
			Object[] dateInfo = dates.toArray();

			Date dateval = getStandardCleanDate((String) dateInfo[0], zone);

			if (isValidDate(dateval)) {

				return dateval;

			} else {
				Calendar calandar = Calendar.getInstance();
				calandar.add(Calendar.HOUR, -2);
				dateval = calandar.getTime();
				return dateval;
			}
		} else {
			for (String datestring : dates) {

				try {

					datesidentified.add(getStandardCleanDate(datestring, zone));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(datesidentified);

		Collections.reverse(datesidentified);

		if (datesidentified.size() > 0) {
			while (dateindex < datesidentified.size() && !isValidDate(datesidentified.get(dateindex))) {
				dateindex++;
			}

			return (dateindex < datesidentified.size()) ? datesidentified.get(dateindex) : datesidentified.get(dateindex - 1);

		} else {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.add(Calendar.HOUR, -2);

			return cal.getTime();
		}
	}

	private static boolean isValidDate(Date dateToBeChecked) {
		Calendar calendar = Calendar.getInstance();
		// v2_Article_extraction_dec_21
		calendar.add(Calendar.MINUTE, -10);

		Date currentDate = calendar.getTime();
		// checking that date must be less than current time stamp
		boolean statusTobeReturned = (currentDate.before(dateToBeChecked)) ? false : true;
		return statusTobeReturned;
	}

	private void dateCountMap(Map<String, Integer> mapForDate, String dateInfoString) {
		Integer count = 0;

		if (!mapForDate.containsKey(dateInfoString)) {
			count = 1;

		} else {
			count = mapForDate.get(dateInfoString);
			count++;
		}
		mapForDate.put(dateInfoString, count);
	}

	// converts given minutes hour format to given format
	private String normalisedMinuteHourInfo(String defaluthourMinutes) {
		if (defaluthourMinutes.length() != 8) {
			if (defaluthourMinutes.length() > 8) {
				defaluthourMinutes = defaluthourMinutes.substring(0, 8);
			} else {
				if (defaluthourMinutes.length() == 6) {
					defaluthourMinutes = defaluthourMinutes + "00";
				} else if (defaluthourMinutes.length() == 5) {
					defaluthourMinutes = defaluthourMinutes + ":00";
				} else if (defaluthourMinutes.length() == 7) {
					defaluthourMinutes = defaluthourMinutes + "0";
				} else {
					String dateComponents[] = defaluthourMinutes.split(":");
					if (dateComponents[0].length() == 1) {
						dateComponents[0] = "0" + dateComponents[0];
					}

					if (dateComponents[1].length() == 1) {
						dateComponents[0] = "0" + dateComponents[0];
					}

					defaluthourMinutes = dateComponents[0] + ":" + dateComponents[1] + ":00";
				}
			}
		}
		return defaluthourMinutes;
	}

	public Date getStandardCleanDate(String textToBeConverted, String zone) {
		int begIndexForYear = 0;
		int endIndexForYear = 0;
		int begIndexForMonth = 0;
		int endIndexForMonth = 0;
		int begIndexForDate = 0;
		int endIndexForDate = 0;
		int begIndexForHourInfo = 0;
		int endIndexForHourInfo = 0;
		int endIndexForAmPm = 0;
		Integer numericYear = null;
		Integer numericMonth = null;
		Integer numericDate = null;
		boolean ampmInformation = false;
		Calendar cal = Calendar.getInstance();
		numericYear = cal.get(Calendar.YEAR);
		numericMonth = cal.get(Calendar.MONTH) + 1;

		numericDate = cal.get(Calendar.DATE);
		String defaultZone = StringUtils.isBlank(zone) ? "UTC" : zone;
		String defalutyear = numericYear.toString();
		String defalutDate = numericDate.toString().length() == 1 ? ("0" + numericDate.toString()) : numericDate.toString();
		numericDate = null;

		String defaluthourMinutes = "00:00:00";
		String htmlDocument = "";
		String amPmInfo = "";
		htmlDocument = textToBeConverted;
		if (isPageContainHourMinuteInfo(htmlDocument)) {

			Matcher matcherForHourMinuteInfo = DateExtractor.patternForHourMinuteInfo.matcher(htmlDocument);
			if (matcherForHourMinuteInfo.find()) {
				begIndexForHourInfo = matcherForHourMinuteInfo.start();
				int endIndexForHourInfoTemp = matcherForHourMinuteInfo.end();
				if (endIndexForHourInfo == 0) {
					defaluthourMinutes = htmlDocument.substring(begIndexForHourInfo, endIndexForHourInfoTemp);
					endIndexForHourInfo = endIndexForHourInfoTemp;
				}
			}
			String timeZoneRegion = htmlDocument.substring(endIndexForHourInfo, Math.min(endIndexForHourInfo + 20, htmlDocument.length()));
			Matcher matcherForZoneIdentification = DateExtractor.patternForZoneIdentification.matcher(timeZoneRegion);
			if (matcherForZoneIdentification.find()) {
				int zoneInfoBeginIndex = matcherForZoneIdentification.start();
				int zoneInfoEndIndex = matcherForZoneIdentification.end();
				defaultZone = timeZoneRegion.substring(zoneInfoBeginIndex, zoneInfoEndIndex);
			}
			String amPmDataregion = htmlDocument.substring(endIndexForHourInfo, Math.min(endIndexForHourInfo + 5, htmlDocument.length()));
			Matcher matcherForAmPm = DateExtractor.patternForAmPmInfo
					.matcher(htmlDocument.substring(endIndexForHourInfo, Math.min(endIndexForHourInfo + 5, htmlDocument.length())));
			ampmInformation = matcherForAmPm.find();
			if (ampmInformation) {
				int startIndexForAmPm = matcherForAmPm.start();
				int endIndexForAmPmtemp = matcherForAmPm.end();
				if (endIndexForAmPm == 0) {
					amPmInfo = amPmDataregion.substring(startIndexForAmPm, endIndexForAmPmtemp);
					endIndexForAmPm = endIndexForAmPmtemp;
				}
			}
			defaluthourMinutes = normalisedMinuteHourInfo(defaluthourMinutes);
			String pretextContainingInformation = htmlDocument.substring(Math.max(begIndexForHourInfo - 30, 0), begIndexForHourInfo);
			// checks date whether Date format like yyyy-MM-DD or DD-MM-yyyy
			if (isDateContainsNumericYearDateFormat(pretextContainingInformation)) {
				Matcher matcherForNumericYearDateFormat = DateExtractor.patternForNumericYearDateFormat.matcher(pretextContainingInformation);
				if (matcherForNumericYearDateFormat.find()) {
					int start = matcherForNumericYearDateFormat.start();
					int end = matcherForNumericYearDateFormat.end();
					String dateYearMonth = pretextContainingInformation.substring(start, end);
					String datefields[] = dateYearMonth.split("\\D");
					if (datefields[0].length() == 4) {
						String temp = datefields[0];
						datefields[0] = datefields[2];
						datefields[2] = temp;
					}
					numericYear = Integer.parseInt(datefields[2]);
					if (numericMonth == Integer.parseInt(datefields[0])) {
						defalutDate = datefields[1];
					} else if (Integer.parseInt(datefields[1]) > 12) {
						numericMonth = Integer.parseInt(datefields[0]);
						defalutDate = datefields[1];
					} else {
						defalutDate = datefields[0];
						numericMonth = Integer.parseInt(datefields[1]);
					}

				}
			} else {
				String textcontainingMonthDateInfo = null;
				Matcher matcherForYearIndate = DateExtractor.patternforYearInDate.matcher(pretextContainingInformation);
				if (matcherForYearIndate.find()) {
					begIndexForYear = matcherForYearIndate.start();
					endIndexForYear = matcherForYearIndate.end();
					String yearFound = pretextContainingInformation.substring(begIndexForYear, endIndexForYear);
					textcontainingMonthDateInfo = pretextContainingInformation.replaceFirst(yearFound.substring(0, yearFound.length() - 1), "");
				} else {
					textcontainingMonthDateInfo = pretextContainingInformation;
				}
				Matcher mathcerForNumericDate = DateExtractor.patternForDateWithNonDigitCharactes.matcher(textcontainingMonthDateInfo);
				if (mathcerForNumericDate.find()) {
					int begDateInfo = mathcerForNumericDate.start();
					int endDateInfotemp = mathcerForNumericDate.end();
					if (endIndexForDate == 0) {
						defalutDate = textcontainingMonthDateInfo.substring(begDateInfo, begDateInfo + 2);
						defalutDate = defalutDate.replaceAll("[\\D]", "");
						endIndexForDate = endDateInfotemp;
					}
					Matcher matcherForMonth = DateExtractor.patternForMonthName.matcher(textcontainingMonthDateInfo);
					if (matcherForMonth.find()) {
						begIndexForMonth = matcherForMonth.start();
						int endIndexForMonthtemp = matcherForMonth.end();
						if (endIndexForMonth == 0) {
							String monthInfo = textcontainingMonthDateInfo.substring(begIndexForMonth, endIndexForMonthtemp);
							numericMonth = DateExtractor.monthsdata.get(monthInfo.substring(0, 3).toLowerCase());
							endIndexForMonth = endIndexForMonthtemp;
						}
					}

					String dateObject = defalutDate + "-" + numericMonth + "-" + numericYear + "~" + defaluthourMinutes + "~" + amPmInfo + "~" + defaultZone;
					constructDateFromDateObject(dateObject, ampmInformation);
				} else {

					Matcher matcherForMonthName = DateExtractor.patternForMonthName.matcher(pretextContainingInformation);
					if (matcherForMonthName.find()) {
						begIndexForMonth = matcherForMonthName.start();
						endIndexForMonth = matcherForMonthName.end();
						String monthName = pretextContainingInformation.substring(begIndexForMonth, endIndexForMonth);
						numericMonth = DateExtractor.monthsdata.get(monthName.toLowerCase());
						String textBetweenMonthAndHours = pretextContainingInformation.substring(endIndexForMonth, begIndexForHourInfo);
						// textBetweenMonthAndHours.
						Matcher matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes.matcher(textBetweenMonthAndHours);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							defalutDate = textBetweenMonthAndHours.substring(begIndexForDate, endIndexForDate).substring(0, 2);
						} else {
							String remainingStrigForDateLookUp = pretextContainingInformation.substring(0, begIndexForMonth);
							matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes.matcher(remainingStrigForDateLookUp);
							if (matcherForDate.find()) {
								begIndexForDate = matcherForDate.start();
								endIndexForDate = matcherForDate.end();
								defalutDate = remainingStrigForDateLookUp.substring(begIndexForDate, endIndexForDate).substring(0, 2);
							}
						}

					}
				}
			}

		} else if (isPageContainsCurrentYearInfo(htmlDocument)) {
			Matcher matcherForYear = DateExtractor.patternforYearInDate.matcher(htmlDocument);
			if (matcherForYear.find()) {
				begIndexForYear = matcherForYear.start();
				endIndexForYear = matcherForYear.end();
				defalutyear = htmlDocument.substring(begIndexForYear, endIndexForYear);
				String textToBeSearched = htmlDocument.replace(defalutyear, "");

				Matcher matcherForMonth = DateExtractor.patternForMonthName.matcher(textToBeSearched);
				if (matcherForMonth.find()) {
					begIndexForMonth = matcherForMonth.start();
					int endIndexForMonthtemp = matcherForMonth.end();
					if (endIndexForMonth == 0) {
						String monthInfo = textToBeSearched.substring(begIndexForMonth, endIndexForMonthtemp);
						numericMonth = DateExtractor.monthsdata.get(monthInfo.substring(0, 3).toLowerCase());
						endIndexForMonth = endIndexForMonthtemp;
						// once you identify month now look for date
						String postTextMayContainDateInfo = textToBeSearched.substring(endIndexForMonth,
								Math.min(endIndexForMonth + 5, textToBeSearched.length()));
						String pretextmayContainDateInfo = textToBeSearched.substring(Math.max(begIndexForMonth - 5, 0), begIndexForMonth);
						Matcher matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes.matcher(pretextmayContainDateInfo);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							String textContaningDate = pretextmayContainDateInfo.substring(begIndexForDate, endIndexForDate);
							defalutDate = textContaningDate.replaceAll("\\D", "");
						} else {
							matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes.matcher(postTextMayContainDateInfo);
							if (matcherForDate.find()) {
								begIndexForDate = matcherForDate.start();
								endIndexForDate = matcherForDate.end();
								String textContaningDate = postTextMayContainDateInfo.substring(begIndexForDate, endIndexForDate);
								defalutDate = textContaningDate.replaceAll("\\D", "");
							}
						}

					}
				} else {
					Matcher matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes.matcher(textToBeSearched);
					if (matcherForDate.find()) {
						begIndexForDate = matcherForDate.start();
						endIndexForDate = matcherForDate.end();
						String textContaningDate = textToBeSearched.substring(begIndexForDate, endIndexForDate);
						defalutDate = textContaningDate.replaceAll("\\D", "");
						defalutDate = defalutDate.replaceAll("\\D", "");
						defalutDate = defalutDate.substring(0, Math.min(defalutDate.length(), 2));
					}
				}

				if (defalutyear.length() > 4) {
					defalutyear = defalutyear.substring(0, 4);

				}
			}
		}
		String dateObject = defalutDate + "-" + numericMonth + "-" + numericYear + "~" + defaluthourMinutes + "~" + amPmInfo + "~" + defaultZone;
		return constructDateFromDateObject(dateObject, ampmInformation);
	}

	public Date constructDateFromDateObject(String dataToBeConverted, boolean ampmInfo) {
		boolean isAmPmInfoAvailable = ampmInfo;
		Date dateTobeReturned = null;
		String dateFormat = "dd-MM-yyyy ";
		String hourInfo = "hh:mm:ss a";
		if (!isAmPmInfoAvailable) {
			hourInfo = "HH:mm:ss";
		}
		if (dataToBeConverted != null && dataToBeConverted.contains("~")) {
			String datevalue[] = dataToBeConverted.split("~");
			Calendar calender = Calendar.getInstance();
			Integer currentYearInfo = calender.get(Calendar.YEAR);
			if (datevalue[1].startsWith(currentYearInfo.toString())) {
				datevalue[1] = StringUtils.reverse(datevalue[1]);
			}
			dataToBeConverted = datevalue[0] + " " + datevalue[1];
			if (ampmInfo) {
				dataToBeConverted = dataToBeConverted + " " + datevalue[2];
			}
			SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat + hourInfo);
			dateFormatter.setTimeZone(TimeZone.getTimeZone(datevalue[3]));
			try {
				dateTobeReturned = dateFormatter.parse(dataToBeConverted);

			} catch (ParseException e) {

				e.printStackTrace();
			}

		}

		if (dateTobeReturned == null) {
			Calendar calender = Calendar.getInstance();
			calender.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateTobeReturned = calender.getTime();
		}

		return dateTobeReturned;
	}

	public boolean isPageContainHourMinuteInfo(String htmldata) {
		Matcher matcherForHourMinute = DateExtractor.patternForHourMinuteInfo.matcher(htmldata);
		return matcherForHourMinute.find();
	}

	public boolean isPageContainsCurrentYearInfo(String htmldata) {
		Matcher matcherForYear = DateExtractor.patternforYearInDate.matcher(htmldata);
		return matcherForYear.find();
	}

	public static boolean isDateContainsNumericYearDateFormat(String textData) {
		Matcher matcherForNumericYearDate = DateExtractor.patternForNumericYearDateFormat.matcher(textData);
		return matcherForNumericYearDate.find();
	}

	public String getImage(Elements htmlDoc) {

		if (htmlDoc != null) {
			Elements elems = htmlDoc.select("img");
			Set<String> imageUrls = new LinkedHashSet<String>();
			for (Element element : elems) {
				String imageUrl = null;
				// if (StringUtils.isEmpty(imageUrl)) {
				String imageData = element.toString();
				List<String> imgAttrs = Utils.getMatchedStringsForGivenText(ApplicationConstants.PATTERN_FOR_ATTR_DATA, imageData);

				for (String imageAttribute : imgAttrs) {

					Matcher matcherForTextInAttr = PAT_FOR_TEXT_IN_ATTR.matcher(imageAttribute);
					int startIndx = 0;
					int endIndx = imageAttribute.length() - 1;
					if (matcherForTextInAttr.find()) {
						startIndx = matcherForTextInAttr.start();
						// endIndx = matcherForTextInAttr.end();
					}
					if (startIndx == endIndx) {
						return null;
					}
					imageUrl = imageAttribute.substring(startIndx + 2, endIndx);
					if (!imageUrl.startsWith("http")) {
						imageUrl = Utils.getAbsoluteUrl(this.Url, imageUrl);
					}
					if (imageUrl != null)
						imageUrls.add(imageUrl);
				}

			}
			return getMainImage(imageUrls);
		} else {
			return null;
		}
	}

	private String getEncodedImageurlUrlContent(Document doc, Map<String, String> imageUrlKeyVlaueMap) {
		StringBuilder htmlDocBuilder = new StringBuilder();
		String document = doc.toString();
		int keygen = 1;
		int start = 0;
		int end = document.length();

		Matcher mathcerForImage = ApplicationConstants.patternForImageExtraction.matcher(document);
		int startIndx = 0;
		int endIndx = document.length();
		while (mathcerForImage.find()) {

			start = mathcerForImage.start();

			end = mathcerForImage.end();

			String encodingVal = "<p>" + ApplicationConstants.encodingForImageTags + keygen++ + "</p>";
			String imageTag = document.substring(start, end);
			imageUrlKeyVlaueMap.put(encodingVal, imageTag);
			String pretext = document.substring(startIndx, start);
			htmlDocBuilder.append(pretext + encodingVal);
			startIndx = end;
		}
		htmlDocBuilder.append(document.substring(startIndx, endIndx));

		return htmlDocBuilder.toString();
		// return doc.toString();
	}

	private String getMainImage(Set<String> images) {
		BufferedImage image = null;
		String mainImageUrl = null;
		URL imageResourceUrl = null;
		int maxSizeImageUrl = 0;
		int size = 0;
		for (String imageurl : images) {

			try {

				imageResourceUrl = new URL(imageurl);
				final HttpURLConnection connection = (HttpURLConnection) imageResourceUrl.openConnection();
				connection.setRequestProperty(ApplicationConstants.USER_AGENT, ApplicationConstants.USER_AGENT_VALUES);
				image = ImageIO.read(connection.getInputStream());
				if (image != null)
					size = image.getHeight() * image.getWidth();
			} catch (IOException e) {
				size = 0;
				e.printStackTrace();
			}
			if (size > maxSizeImageUrl && (image != null) && image.getHeight() > ApplicationConstants.MINIMUM_HEIGHT_OF_IMAGE) {
				maxSizeImageUrl = size;
				mainImageUrl = imageurl;
			}

		}
		return mainImageUrl;

	}

	public String getImage() {
		return mainImageUrl;
	}
	private boolean isInValidAnchorElement(Element element){
	  int textLength=element.text().length();
	  if(textLength>35&&textLength<80){
	    return true;
	  }else{
	    return false;
	  }
	}
	/*
	 * public String getImage(byte[] htmlInBytes, int browsePageCode) { String
	 * htmldoc = new String(htmlInBytes); Document doc = Jsoup.parse(htmldoc);
	 * Elements imagesImatched = doc.select("img"); for (Element element :
	 * imagesImatched) { if(element.attr("src")!=null) {} } return null; }
	 */
}
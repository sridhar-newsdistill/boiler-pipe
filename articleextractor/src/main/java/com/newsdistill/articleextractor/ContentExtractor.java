package com.newsdistill.articleextractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import static com.newsdistill.articleextractor.ApplicationConstants.*;

public class ContentExtractor implements BaseArticleExractor {
	StringTokenizer titleTokens = new StringTokenizer(titleTags, "|");

	private ArticleContent contentIdentified = new ArticleContent();
	private String Url;
	private Charset cs = null;

	public ContentExtractor() {
		cs = Charset.forName("UTF-8");
	}

	public ContentExtractor(String url) {
		this.Url = url;
		cs = Charset.forName("UTF-8");
	}

	// 1idea try to create multile threads used exhisting
	@Override
	public ArticleContent getTotoalContent() {
		URL pagelink = null;
		byte[] htmlBytes = null;

		try {
			Map<String, Object> resultMap = HTMLFetcherUtil
					.getBytesFromURL(new URL(this.Url));
			htmlBytes = (byte[]) resultMap.get("bytes");
			this.cs = (Charset) resultMap.get("charset");
			if (this.cs == null) {
				this.cs = Charset.forName("UTF-8");
			}
		} catch (MalformedURLException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		String contentAvailableFrom = new String(htmlBytes);
		contentIdentified.setUrl(this.Url);
		contentIdentified.setDomain(getDomain());
		contentIdentified.setImageUrl(getImage(contentAvailableFrom));
		contentIdentified.setTitle(getTitle(contentAvailableFrom));

		try {
			contentIdentified.setDescription(getDescription(new URL(this.Url),
					htmlBytes));
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IndexOutOfBoundsException arrayIndexRange) {
			arrayIndexRange.printStackTrace();
		}

		// should spawn 4 thereads
		// ExecutorService es = Executors.newFixedThreadPool(4);
		contentIdentified.setArticleDate(getDate(contentAvailableFrom));

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
			Elements elements = htmldoc.select(value);
			if (elements == null || elements.first() == null) {
				continue;
			} else {
				for (Element element : elements) {
					if (!StringUtils.isBlank(element.text())
							&& element.text().length() > 15) {
						doucmentTitle = element.html();
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

	public String getDescription(URL url) {
		ArticleExtractor ce = null;
		ce = CommonExtractors.ARTICLE_EXTRACTOR;
		final HTMLHighlighter contentHighlighter = HTMLHighlighter
				.newHighlightingInstance();

		String resultFromBoilerPipe = "";
		try {
			resultFromBoilerPipe = contentHighlighter.process(url, ce);
			
		} catch (IOException e) {

			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		}
		resultFromBoilerPipe = "<html><head></head><body>"
				+ resultFromBoilerPipe + "</body></html>";

		resultFromBoilerPipe.replaceFirst("(display:none)(;)?", "");
		
		return resultFromBoilerPipe; // getCleanedDescription(resultFromBoilerPipe);
	}

	public String getDescription(URL url, byte[] contentInBytes) {
		ArticleExtractor ce = null;
		ce = CommonExtractors.ARTICLE_EXTRACTOR;
		final HTMLHighlighter contentHighlighter = HTMLHighlighter
				.newHighlightingInstance();

		String resultFromBoilerPipe = "";
		try {

			resultFromBoilerPipe = contentHighlighter.process(url, ce,
					contentInBytes, this.cs);

		} catch (IOException e) {

			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		}
		resultFromBoilerPipe = "<html><head></head><body>"
				+ resultFromBoilerPipe + "</body></html>";
		/*
		 * resultFromBoilerPipe = resultFromBoilerPipe.replaceAll("<BR>",
		 * encodingForLineBreaks); resultFromBoilerPipe =
		 * resultFromBoilerPipe.replaceAll("</BR>", encodingForLineBreaks);
		 */// getcleanDescriptionFromJsoup(resultFromBoilerPipe);
		return resultFromBoilerPipe; // getCleanedDescription(resultFromBoilerPipe);
	}

	// get cleaned description :

	// breadth first travel

	/*
	 * private String getcleanDescriptionFromJsoup(String boilerPipeData) {
	 * boilerPipeData = boilerPipeData.replaceAll(REGEX_FOR_EMPTY_TAG_REMOVAL,
	 * ""); Document doc = Jsoup.parse(boilerPipeData);
	 * 
	 * Elements elements = doc.children(); List<NodeInfo> list = new
	 * ArrayList<>();
	 * 
	 * return null; }
	 */
	// cleans up the description
	private String getCleanedDescription(String htmlString) {

		Map<String, String> numberedTagWithContent = new LinkedHashMap<String, String>();
		List<String> tagWithWordCount = new ArrayList<String>();
		int absposition = 0;
		int tagnumber = 0;
		int begindex = 0;
		int lengthOfString = htmlString.length();
		String tagName = null;
		String tagNameWithoutAttributes = null;
		Matcher matcherForStart = patternForStartTag.matcher(htmlString);

		while (absposition < lengthOfString - 4) {

			String startString = htmlString;

			matcherForStart = patternForStartTag.matcher(htmlString);
			Matcher mathcerForEnd = patternForEndTag.matcher(htmlString);
			if (isBeginningOfTag(startString)) {
				if (matcherForStart.find()) {
					int position = begindex = matcherForStart.start();
					int endIndex = matcherForStart.end();
					tagName = htmlString.substring(position, endIndex);
					Matcher matcherForExactTagName = patternForFindingExactTagName
							.matcher(tagName);
					if (matcherForExactTagName.find()) {
						String endTag = tagName.substring(
								matcherForExactTagName.start(),
								matcherForExactTagName.end());
						tagNameWithoutAttributes = "</"
								+ endTag.substring(1, endTag.length()) + ">";
					}
					tagnumber++;
					htmlString = htmlString.substring(endIndex);
					absposition = absposition + tagName.length();
				}

			}
			if (isEndofTheTag(htmlString)) {
				if (mathcerForEnd.find()) {

					int position = begindex = mathcerForEnd.start();

					int endIndex = mathcerForEnd.end();
					tagName = htmlString.substring(position, endIndex + 1);
					absposition += tagName.length();
					htmlString = htmlString.substring(endIndex);
				}

			} else {
				if (isBeginningOfTag(htmlString)) {
					continue;
				}
				if (tagName.startsWith("</")) {
					tagName = noTagEncoding;
					tagnumber++;
				}
				Matcher mathcerForDelimiterForOpenAndClosingElement = patternForBeginningHtmlElement
						.matcher(htmlString);
				if (mathcerForDelimiterForOpenAndClosingElement.find()) {
					int endIndex = mathcerForDelimiterForOpenAndClosingElement
							.start();
					String content = htmlString.substring(0, endIndex);
					content = content.trim();
					int numberofWordsInContent = Utils.getWordCount(content);
					htmlString = htmlString.substring(endIndex);
					content = tagName + content + tagNameWithoutAttributes;

					numberedTagWithContent.put(tagName + TAG_NAME_TAGNUM_DELIM
							+ tagnumber, content);
					tagWithWordCount.add(tagName + TAG_NAME_TAGNUM_DELIM
							+ tagnumber + TAG_CONTENT_WORDCNT_DELIM
							+ numberofWordsInContent);

					absposition += endIndex;

				}
			}
		}

		// this should be configurable
		return getFinalContent(numberedTagWithContent, tagWithWordCount, false);
	}

	@Override
	public Date getDate(String content) {

		// content = "<html><head></head><body>" + content + "</body></html>";
		Document doc = Jsoup.parse(content);

		return getFinalDate(doc);
	}

	/*
	 * public Date getDate(Document doc) { return getFinalDate(doc); }
	 */

	@Override
	public String getImage(String conent) {
		// TODO Auto-generated method stub

		Document doc = null;
		Elements nodes = null;
		String imageUrl = null;
		// Connection connection = Jsoup.connect(url);
		doc = Jsoup.parse(conent);
		String imagePattern = "meta[property=og:image]|img[src]|img[data*]";
		StringTokenizer st = new StringTokenizer(imagePattern, "|");
		doc.select("meta[property=og:image]");
		while (st.hasMoreElements()) {

			nodes = doc.select(st.nextElement().toString());
			// System.out.println(nodes);
			if (nodes == null) {
				System.out.println("caught up here");
				continue;
			}

			Element imageTag = nodes.first();

			if (imageTag != null) {
				if (imageTag.tagName().equalsIgnoreCase("meta")) {
					imageUrl = imageTag.attr("content");
					// System.out.println("image");
					// System.out.println(imageUrl);
				}

				break;
			}

		}
		return imageUrl;

	}

	@Override
	public String getLogo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain() {
		String domain = null;
		try {
			URL articleUrl = new URL(Url);
			domain = articleUrl.getHost();
			// System.out.println(domain);
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}

		return domain;
	}

	private boolean isBeginningOfTag(String remainingString) {

		if (!StringUtils.isEmpty(remainingString)) {

			Matcher matcherForStartOfTheTag = patternForStartTag

			.matcher(remainingString);

			boolean truthval = matcherForStartOfTheTag.find();

			// System.out.println(truthval);

			return truthval;

		} else {

			return false;
		}
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

	private String getFinalContent(Map<String, String> tagContent,
			List<String> tagContentLenghts, boolean removeAnchorTag) {
		// TODO
		// Externalis e these properties - start

		int toleranceCount = 3;
		// boolean removeAnchorTag = false;

		// Externalize these properties - end
		List<String> sortedList = new ArrayList<String>(tagContentLenghts);

		TagCountComparator tg = new TagCountComparator();

		Collections.sort(sortedList, tg);

		int totalWordCount = 0;

		int loopSize = 0;

		for (String string : sortedList) {

			if (loopSize <= sortedList.size()) {

				String tagNameWithIndex = (string
						.split(TAG_CONTENT_WORDCNT_DELIM)[0]).trim();

				totalWordCount = totalWordCount

				+ Utils.getWordCount(tagContent.get(tagNameWithIndex));
			} else {

				break;

			}

		}

		String tagWithMaxDescription = sortedList.get(0);

		List<TagMetaData> tagMetaDatas = new ArrayList<TagMetaData>();

		TagMetaData bestMatchMetaData = null;

		int index = 0;

		for (String metadata : tagContentLenghts) {

			TagMetaData tagMetaData = new TagMetaData(metadata);
			String text = tagContent.get(tagMetaData.getTagNameWithIndex());
			if (text.trim().length() == 1 || StringUtils.isBlank(text.trim())) {
				continue;
			}

			if (removeAnchorTag
					&& tagMetaData.getTagName().toLowerCase().contains("<a>")) {
				continue;
			}
			String content = text.trim().replaceAll("1922135", "</br>");
			content = content.replaceAll("</br>[\\s]*<\br>", "1922135");
			content = content.replaceAll(noTagEncoding, "");
			content = content.replaceAll(
					"</" + noTagEncoding.substring(1, noTagEncoding.length()),
					"");
			tagMetaData.setText(content.trim().replaceAll("1922135", "</br>"));
			tagMetaData.setWordCount(Utils.getWordCount(text.trim()));
			tagMetaData.setIndex(index);
			index++;

			if (metadata.equalsIgnoreCase(tagWithMaxDescription)) {
				tagMetaData.setPartOfDescription(true);
				bestMatchMetaData = tagMetaData;
			}
			tagMetaDatas.add(tagMetaData);
		}

		int avgWordCount = (int) Math.ceil(totalWordCount
				/ (tagContentLenghts.size() / 2));

		boolean traverseForward = true;
		boolean traverseBackward = true;
		if (bestMatchMetaData.getIndex() == tagMetaDatas.size()) {
			traverseForward = false;
		}

		if (bestMatchMetaData.getIndex() == 0) {
			traverseBackward = false;
		}

		int bestMatchIndex = bestMatchMetaData.getIndex();
		index = bestMatchIndex;

		if (traverseForward) {
			while (index < tagMetaDatas.size()) {
				index++;
				if (index == tagMetaDatas.size()) {
					break;
				}
				int currentIndex = index;
				TagMetaData currentTag = tagMetaDatas.get(currentIndex);
				if ((currentTag.getWordCount() > Math.ceil(0.4 * avgWordCount))) {
					tagMetaDatas.get(currentIndex).setPartOfDescription(true);
					enableDescriptionFlag(currentIndex, tagMetaDatas,
							toleranceCount, true);
				} else {
					if (toleranceCheck(currentIndex, tagMetaDatas,
							toleranceCount, true) == false) {
						break;
					}
				}

			}
		}

		index = bestMatchMetaData.getIndex();
		if (traverseBackward) {
			while (index >= 0) {
				index--;
				if (index == 0) {
					break;
				}
				int currentIndex = index;
				TagMetaData currentTag = tagMetaDatas.get(currentIndex);
				if (currentTag.getText().matches("^(</br>)+")) {
					tagMetaDatas.get(currentIndex).setPartOfDescription(true);
					continue;
				}

				if ((currentTag.getWordCount() > Math.ceil(0.4 * avgWordCount))) {
					tagMetaDatas.get(currentIndex).setPartOfDescription(true);
					enableDescriptionFlag(currentIndex, tagMetaDatas,
							toleranceCount, false);
				} else {
					if (toleranceCheck(currentIndex, tagMetaDatas,
							toleranceCount, false) == false) {
						break;
					}
				}

			}
		}

		StringBuilder description = new StringBuilder();
		for (TagMetaData tagMetaData : tagMetaDatas) {
			if (tagMetaData.isPartOfDescription() == true) {
				description.append(tagMetaData.getText());
			}
		}

		String metaDataForTitle = searchTitleMetaData(tagContentLenghts);
		if (!StringUtils.isEmpty(metaDataForTitle)) {
			/*
			 * System.out.println(" title" +
			 * tagContent.get(metaDataForTitle.split(":")[0]) + "end of title");
			 */
		}
		// System.out.println(description.toString().replaceAll("[\\s]+", " "));

		return description.toString().replaceAll("[\\s]+", " ");
	}

	private void enableDescriptionFlag(int currentIndex,
			List<TagMetaData> tagMetaDatas, int toleranceCount,
			boolean traverseForward) {
		try {
			if (traverseForward) {
				for (int i = 1; i <= toleranceCount; i++) {
					if (currentIndex - i <= 0) {
						return;
					}

					tagMetaDatas.get(currentIndex - i).setPartOfDescription(
							true);
				}
			} else {
				for (int i = 1; i <= toleranceCount; i++) {
					if (currentIndex + i >= tagMetaDatas.size()) {
						return;
					}
					tagMetaDatas.get(currentIndex + i).setPartOfDescription(
							true);
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			// do nothing
		}

	}

	private boolean toleranceCheck(int currentIndex,
			List<TagMetaData> tagMetaDatas, int toleranceCount,
			boolean traverseForward) {
		int index = 0;
		// int skipCount = 0;

		if (traverseForward) {
			while (true) {
				if (tagMetaDatas.get(currentIndex - index)
						.isPartOfDescription() == false) {
					// skipCount++;
				} else {
					return true;
				}
				index++;

			}
		} else {
			while (true) {
				if (tagMetaDatas.get(currentIndex + index)
						.isPartOfDescription() == false) {
					// skipCount++;
				} else {
					return true;
				}
				index++;

			}

		}
	}

	private String searchTitleMetaData(List<String> tagStructure) {
		int index = 0;
		String metaData = null;
		while (index < tagStructure.size()) {
			if (tagStructure.get(index).toLowerCase().contains("h1")
					|| tagStructure.get(index).toLowerCase().contains("h2")
					|| tagStructure.get(index).toLowerCase().contains("h3")
					|| tagStructure.get(index).toLowerCase().contains("title")) {
				metaData = tagStructure.get(index);
				break;
			} else {
				index++;
			}
		}
		return metaData;
	}

	public static boolean isContent(String remainingString, int pos) {

		return false;
	}

	private Date getFinalDate(Document doc) {
		Elements elems = null;
		int dateindex = 0;
		Map<String, Integer> mapForDate = new LinkedHashMap<String, Integer>();
		Pattern pattern = Pattern.compile(DateExtractor.regexForSelectiontags);
		elems = doc.getAllElements();
		Elements allSelectedElements = elems.clone();
		allSelectedElements = allSelectedElements
				.select(DateExtractor.regexForSelectiontags);
		for (Element e : allSelectedElements) {

			if (e.text().toLowerCase().contains("2015")
					&& e.text().length() < 100) {
				dateCountMap(mapForDate, e.text());
			}
		}
		for (Element block : elems) {
			Attributes node = block.attributes();
			Iterator<Attribute> it = node.iterator();
			while (it.hasNext()) {

				// Pattern patternval = Pattern.compile(dateWhileListTags);
				Attribute attr = it.next();
				String val = attr.getValue();
				val = val.toLowerCase();
				Matcher matcher = DateExtractor.patternval.matcher(val);

				if ((matcher.find() || !StringUtils.isEmpty(val))
						&& (val.toLowerCase().contains("date") || val
								.toLowerCase().contains("modified"))) {
					String probableDate = block.attr("content");
					if (!StringUtils.isEmpty(probableDate)
							&& (probableDate.toLowerCase().contains("2015") && probableDate
									.length() < 100)) {

						dateCountMap(mapForDate, probableDate);
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

			Date dateval = getStandardCleanDate((String) dateInfo[0]);

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

					datesidentified.add(getStandardCleanDate(datestring));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(datesidentified);

		Collections.reverse(datesidentified);
		/*
		 * for (Date dateobj : datesidentified) {
		 * System.out.println(dateobj.toString()); }
		 */

		while (!isValidDate(datesidentified.get(dateindex))) {

			dateindex++;
		}

		return datesidentified.get(dateindex);
	}

	// counts number of times each date format type appeared

	private static boolean isValidDate(Date dateToBeChecked) {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MINUTE, -10);

		Date currentDate = calendar.getTime();

		// System.out.println();

		// checking that date must be less than current time stamp
		boolean statusTobeReturned = (currentDate.before(dateToBeChecked)) ? false
				: true;
		return statusTobeReturned;
	}

	private void dateCountMap(Map<String, Integer> mapForDate,
			String dateInfoString) {
		Integer count = 0;

		if (!mapForDate.containsKey(dateInfoString)) {
			count = 1;

		} else {
			count = mapForDate.get(dateInfoString);
			count++;
		}
		mapForDate.put(dateInfoString, count);
	}

	public Date getStandardCleanDate(String textToBeConverted) {
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
		String defaultZone = "";
		String defalutyear = numericYear.toString();
		String defalutDate = numericDate.toString().length() == 1 ? ("0" + numericDate
				.toString()) : numericDate.toString();
		numericDate = null;
		String defalutmonth = numericMonth.toString();
		String defaluthourMinutes = "00:00:00";
		String htmlDocument = "";
		String amPmInfo = "";
		htmlDocument = textToBeConverted;
		if (isPageContainHourMinuteInfo(htmlDocument)) {

			Matcher matcherForHourMinuteInfo = DateExtractor.patternForHourMinuteInfo
					.matcher(htmlDocument);
			if (matcherForHourMinuteInfo.find()) {
				begIndexForHourInfo = matcherForHourMinuteInfo.start();
				int endIndexForHourInfoTemp = matcherForHourMinuteInfo.end();
				if (endIndexForHourInfo == 0) {
					defaluthourMinutes = htmlDocument.substring(
							begIndexForHourInfo, endIndexForHourInfoTemp);
					endIndexForHourInfo = endIndexForHourInfoTemp;
				}
			}
			String timeZoneRegion = htmlDocument.substring(endIndexForHourInfo,
					Math.min(endIndexForHourInfo + 20, htmlDocument.length()));
			Matcher matcherForZoneIdentification = DateExtractor.patternForZoneIdentification
					.matcher(timeZoneRegion);
			if (matcherForZoneIdentification.find()) {
				int zoneInfoBeginIndex = matcherForZoneIdentification.start();
				int zoneInfoEndIndex = matcherForZoneIdentification.end();
				defaultZone = timeZoneRegion.substring(zoneInfoBeginIndex,
						zoneInfoEndIndex);
			}
			String amPmDataregion = htmlDocument.substring(endIndexForHourInfo,
					Math.min(endIndexForHourInfo + 5, htmlDocument.length()));
			Matcher matcherForAmPm = DateExtractor.patternForAmPmInfo
					.matcher(htmlDocument.substring(
							endIndexForHourInfo,
							Math.min(endIndexForHourInfo + 5,
									htmlDocument.length())));
			ampmInformation = matcherForAmPm.find();
			if (ampmInformation) {
				int startIndexForAmPm = matcherForAmPm.start();
				int endIndexForAmPmtemp = matcherForAmPm.end();
				if (endIndexForAmPm == 0) {
					amPmInfo = amPmDataregion.substring(startIndexForAmPm,
							endIndexForAmPmtemp);
					endIndexForAmPm = endIndexForAmPmtemp;
				}
			}

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

						defaluthourMinutes = dateComponents[0] + ":"
								+ dateComponents[1] + ":00";
					}
				}
			}

			String pretextContainingInformation = htmlDocument.substring(
					Math.max(begIndexForHourInfo - 30, 0), begIndexForHourInfo);
			// checks date whether Date format like yyyy-MM-DD or DD-MM-yyyy
			if (isDateContainsNumericYearDateFormat(pretextContainingInformation)) {
				Matcher matcherForNumericYearDateFormat = DateExtractor.patternForNumericYearDateFormat
						.matcher(pretextContainingInformation);
				if (matcherForNumericYearDateFormat.find()) {
					int start = matcherForNumericYearDateFormat.start();
					int end = matcherForNumericYearDateFormat.end();
					String dateYearMonth = pretextContainingInformation
							.substring(start, end);
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
				Matcher matcherForYearIndate = DateExtractor.patternforYearInDate
						.matcher(pretextContainingInformation);
				if (matcherForYearIndate.find()) {
					begIndexForYear = matcherForYearIndate.start();
					endIndexForYear = matcherForYearIndate.end();
					String yearFound = pretextContainingInformation.substring(
							begIndexForYear, endIndexForYear);
					textcontainingMonthDateInfo = pretextContainingInformation
							.replaceFirst(yearFound.substring(0,
									yearFound.length() - 1), "");
				} else {
					textcontainingMonthDateInfo = pretextContainingInformation;
				}
				Matcher mathcerForNumericDate = DateExtractor.patternForDateWithNonDigitCharactes
						.matcher(textcontainingMonthDateInfo);
				if (mathcerForNumericDate.find()) {
					int begDateInfo = mathcerForNumericDate.start();
					int endDateInfotemp = mathcerForNumericDate.end();
					if (endIndexForDate == 0) {
						defalutDate = textcontainingMonthDateInfo.substring(
								begDateInfo, begDateInfo + 2);
						defalutDate = defalutDate.replaceAll("[\\D]", "");
						endIndexForDate = endDateInfotemp;
					}
					Matcher matcherForMonth = DateExtractor.patternForMonthName
							.matcher(textcontainingMonthDateInfo);
					if (matcherForMonth.find()) {
						begIndexForMonth = matcherForMonth.start();
						int endIndexForMonthtemp = matcherForMonth.end();
						if (endIndexForMonth == 0) {
							String monthInfo = textcontainingMonthDateInfo
									.substring(begIndexForMonth,
											endIndexForMonthtemp);
							numericMonth = DateExtractor.monthsdata
									.get(monthInfo.substring(0, 3)
											.toLowerCase());
							endIndexForMonth = endIndexForMonthtemp;
						}
					}

					String dateObject = defalutDate + "-" + numericMonth + "-"
							+ numericYear + "~" + defaluthourMinutes + "~"
							+ amPmInfo + "~" + defaultZone;
					constructDateFromDateObject(dateObject, ampmInformation);
				} else {
					/*
					 * pretextContainingInformation =
					 * pretextContainingInformation .replaceFirst(defalutyear,
					 * "");
					 */
					Matcher matcherForMonthName = DateExtractor.patternForMonthName
							.matcher(pretextContainingInformation);
					if (matcherForMonthName.find()) {
						begIndexForMonth = matcherForMonthName.start();
						endIndexForMonth = matcherForMonthName.end();
						String monthName = pretextContainingInformation
								.substring(begIndexForMonth, endIndexForMonth);
						numericMonth = DateExtractor.monthsdata.get(monthName
								.toLowerCase());
						String textBetweenMonthAndHours = pretextContainingInformation
								.substring(endIndexForMonth,
										begIndexForHourInfo);
						// textBetweenMonthAndHours.
						Matcher matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes
								.matcher(textBetweenMonthAndHours);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							defalutDate = textBetweenMonthAndHours.substring(
									begIndexForDate, endIndexForDate)
									.substring(0, 2);
						} else {
							String remainingStrigForDateLookUp = pretextContainingInformation
									.substring(0, begIndexForMonth);
							matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes
									.matcher(remainingStrigForDateLookUp);
							if (matcherForDate.find()) {
								begIndexForDate = matcherForDate.start();
								endIndexForDate = matcherForDate.end();
								defalutDate = remainingStrigForDateLookUp
										.substring(begIndexForDate,
												endIndexForDate)
										.substring(0, 2);
							}
						}

					}
				}
			}

		} else if (isPageContainsCurrentYearInfo(htmlDocument)) {
			Matcher matcherForYear = DateExtractor.patternforYearInDate
					.matcher(htmlDocument);
			if (matcherForYear.find()) {
				begIndexForYear = matcherForYear.start();
				endIndexForYear = matcherForYear.end();
				defalutyear = htmlDocument.substring(begIndexForYear,
						endIndexForYear);
				String textToBeSearched = htmlDocument.replace(defalutyear, "");

				Matcher matcherForMonth = DateExtractor.patternForMonthName
						.matcher(textToBeSearched);
				if (matcherForMonth.find()) {
					begIndexForMonth = matcherForMonth.start();
					int endIndexForMonthtemp = matcherForMonth.end();
					if (endIndexForMonth == 0) {
						String monthInfo = textToBeSearched.substring(
								begIndexForMonth, endIndexForMonthtemp);
						numericMonth = DateExtractor.monthsdata.get(monthInfo
								.substring(0, 3).toLowerCase());
						endIndexForMonth = endIndexForMonthtemp;
						// once you identify month now look for date
						String postTextMayContainDateInfo = textToBeSearched
								.substring(endIndexForMonth, Math.min(
										endIndexForMonth + 5,
										textToBeSearched.length()));
						String pretextmayContainDateInfo = textToBeSearched
								.substring(Math.max(begIndexForMonth - 5, 0),
										begIndexForMonth);
						Matcher matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes
								.matcher(pretextmayContainDateInfo);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							String textContaningDate = pretextmayContainDateInfo
									.substring(begIndexForDate, endIndexForDate);
							defalutDate = textContaningDate.replaceAll("\\D",
									"");
						} else {
							matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes
									.matcher(postTextMayContainDateInfo);
							if (matcherForDate.find()) {
								begIndexForDate = matcherForDate.start();
								endIndexForDate = matcherForDate.end();
								String textContaningDate = postTextMayContainDateInfo
										.substring(begIndexForDate,
												endIndexForDate);
								defalutDate = textContaningDate.replaceAll(
										"\\D", "");
							}
						}

					}
				} else {
					Matcher matcherForDate = DateExtractor.patternForDateWithNonDigitCharactes
							.matcher(textToBeSearched);
					if (matcherForDate.find()) {
						begIndexForDate = matcherForDate.start();
						endIndexForDate = matcherForDate.end();
						String textContaningDate = textToBeSearched.substring(
								begIndexForDate, endIndexForDate);
						defalutDate = textContaningDate.replaceAll("\\D", "");
						defalutDate = defalutDate.replaceAll("\\D", "");
						defalutDate = defalutDate.substring(0,
								Math.min(defalutDate.length(), 2));
					}
				}

				if (defalutyear.length() > 4) {
					defalutyear = defalutyear.substring(0, 4);

				}
			}
		}
		String dateObject = defalutDate + "-" + numericMonth + "-"
				+ numericYear + "~" + defaluthourMinutes + "~" + amPmInfo + "~"
				+ defaultZone;
		return constructDateFromDateObject(dateObject, ampmInformation);
	}

	public Date constructDateFromDateObject(String dataToBeConverted,
			boolean ampmInfo) {
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
			SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat
					+ hourInfo);
			try {
				dateTobeReturned = dateFormatter.parse(dataToBeConverted);
				// System.out.println(dateTobeReturned.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
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
		Matcher matcherForHourMinute = DateExtractor.patternForHourMinuteInfo
				.matcher(htmldata);
		return matcherForHourMinute.find();
	}

	public boolean isPageContainsCurrentYearInfo(String htmldata) {
		Matcher matcherForYear = DateExtractor.patternforYearInDate
				.matcher(htmldata);
		return matcherForYear.find();
	}

	private String getContentFromUrl(String url) {
		StringBuilder sb = new StringBuilder(url);
		InputStream ins = null;
		String data = null;
		try {
			URL pagelink = new URL(url);
			URLConnection urlconn = pagelink.openConnection();
			ins = urlconn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));

			while ((data = br.readLine()) != null) {
				sb.append(data);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				ins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static boolean isDateContainsNumericYearDateFormat(String textData) {
		Matcher matcherForNumericYearDate = DateExtractor.patternForNumericYearDateFormat
				.matcher(textData);
		return matcherForNumericYearDate.find();
	}

}

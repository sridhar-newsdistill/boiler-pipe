package com.newsdistill.articleextractor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.newsdistill.articleextractor.utils.Utils;

public class ContentExtractor implements BaseArticleExractor {
	static String encodingForLineBreaks = "1922135";
	static String encodingForImageTags = "5312219";
	static String regExToFindStartOfTheString = "^<[^>/]*>";
	static String regExForEndOfTheTag = "^</[^>]*>";
	static String delimiterBeforeOpenAndClosingTags = "<";
	static String regexForImageExtraction = "<[\\s]*(IMG|img)[^>]*>";
	static Pattern patternForStartTag = Pattern
			.compile(regExToFindStartOfTheString);
	static Pattern patternForImageExtraction = Pattern
			.compile(regexForImageExtraction);
	static Pattern patternForEndTag = Pattern.compile(regExForEndOfTheTag);
	static Pattern patternForBeginningHtmlElement = Pattern
			.compile(delimiterBeforeOpenAndClosingTags);
	static String regExForimgExtraction = "<(h([1-5]+)|title)";
	static Pattern patternForTitle = Pattern.compile(regExForimgExtraction);
	// in case if we don't find title in these headers we will directly download
	// from url
	static String regexForTitleExtraction = "<(h1|h2|h3|title)[^>]*>";
	static Pattern patternForTitleExtraction = Pattern.compile(
			regexForTitleExtraction, Pattern.CASE_INSENSITIVE);

	private ArticleContent contentIdentified = new ArticleContent();
	private String Url;

	ContentExtractor(String url) {
		this.Url = url;
	}

	// idea try to create multile threads used exhisting
	@Override
	public ArticleContent getTotoalContent(String url) {
		URL pagelink = null;
		ArticleExtractor ce = null;

		// ExecutorService es = Executors.newFixedThreadPool(4);

		/*
		 * List<String> imageUrls = new ArrayList<String>();
		 * 
		 * List<TextBlock> blocks = null;
		 */

		try {
			pagelink = new URL(Url);
			ce = CommonExtractors.ARTICLE_EXTRACTOR;
			final HTMLHighlighter contentHighlighter = HTMLHighlighter
					.newHighlightingInstance();
			try {
				String result = contentHighlighter.process(pagelink, ce);
				result = "<html><head></head><body>" + result
						+ "</body></html>";
				result = result.replaceAll("<BR>", "");
				result = result.replaceAll("</BR>", encodingForLineBreaks);
				// System.out.println(result);
				contentIdentified.setDescription(getDescription(result));

			} catch (BoilerpipeProcessingException | SAXException e) {

				e.printStackTrace();
			}

		} catch (MalformedURLException mfue) {

		} catch (IOException ioe) {

		}
		// should spawn 4 thereads
		return null;
	}

	@Override
	public String getTitle(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	// cleans up the description
	public String getDescription(String htmlString) {

		Map<String, String> numberedTagWithContent = new LinkedHashMap<String, String>();
		List<String> tagWithWordCount = new ArrayList<String>();
		int absposition = 0;
		int tagnumber = 0;
		int begindex = 0;
		int lengthOfString = htmlString.length();
		String tagName = null;
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
					tagnumber++;
					htmlString = htmlString.substring(endIndex);
					absposition = absposition + tagName.length();
				}
				// stack.push(tagName + ":" + tagnumber);

			}
			if (isEndofTheTag(htmlString)) {
				if (mathcerForEnd.find()) {

					int position = begindex = mathcerForEnd.start();

					int endIndex = mathcerForEnd.end();
					tagName = htmlString.substring(position, endIndex);
					absposition += tagName.length();
					htmlString = htmlString.substring(endIndex);
				}
				/*
				 * int pos = stack.getTop(); String lastTag =
				 * stack.getTagInfo().get(pos); if (tagName.substring(2,
				 * tagName.length() - 1) == lastTag .substring(1,
				 * lastTag.length() - 1)) stack.pop();
				 */
			} else {
				if (isBeginningOfTag(htmlString)) {
					continue;
				}
				if (tagName.startsWith("</")) {
					tagName = "<p>";
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
					content = tagName + content + "</"
							+ tagName.substring(1, tagName.length());
					numberedTagWithContent.put(tagName + "-" + tagnumber,
							content);
					tagWithWordCount.add(tagName + "-" + tagnumber + ":"
							+ numberofWordsInContent);
					absposition += endIndex;

				}
			}
		}
		
		//this should be configurable
		return getFinalContent(numberedTagWithContent, tagWithWordCount,
				false);
	}

	@Override
	public String getDate(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImage(String conent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLogo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain() {
		String domain=null;
		try {
			URL articleUrl=new URL(Url);
			domain=articleUrl.getHost();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
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
			// System.out.println(truthval);
			return truthval;
		} else {
			return false;
		}
		// return false;
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
				String tagNameWithIndex = (string.split(":")[0]).trim();
				totalWordCount = totalWordCount
						+ Utils.getWordCount(tagContent.get(tagNameWithIndex));
			} else {
				break;
			}

		}

		String tagWithMaxDescription = sortedList.get(0);

		List<TagMetaData> tagMetaDatas = new ArrayList<>();

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
			tagMetaData.setText(text.trim().replaceAll("1922135", "</br>"));
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
			System.out.println(" title"
					+ tagContent.get(metaDataForTitle.split(":")[0])
					+ "end of title");
		}
		System.out.println(description.toString().replaceAll("[\\s]+", " "));

		return null;
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
		//int skipCount = 0;

		if (traverseForward) {
			while (true) {
				if (tagMetaDatas.get(currentIndex - index)
						.isPartOfDescription() == false) {
				//	skipCount++;
				} else {
					return true;
				}
				index++;

				/*
				 * if(skipCount > toleranceCount){ return false; }
				 */
			}
		} else {
			while (true) {
				if (tagMetaDatas.get(currentIndex + index)
						.isPartOfDescription() == false) {
					//skipCount++;
				} else {
					return true;
				}
				index++;

				/*
				 * if(skipCount > toleranceCount){ return false; }
				 */
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

}

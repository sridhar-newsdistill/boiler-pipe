package com.kohlschutter.boilerpipe.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.kohlschutter.boilerpipe.sax.ImageExtractor;

public class NdBoilerPipe {

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
	//in case if we don't find title in these headers we will directly download from url 
    static String regexForTitleExtraction="<(h1|h2|h3|title)[^>]*>";
    static Pattern patternForTitleExtraction=Pattern.compile(regexForTitleExtraction,Pattern.CASE_INSENSITIVE);
    private static String htmlContentForTitleExtraction=null;
	public static void main(String args[]) throws IOException, SAXException,
			BoilerpipeProcessingException {
		boolean removeAnchorTags = false;
		String regexForAnchorTagRemoval = "<(a|A|(?i)(meta))[\\s]*[^>]*>[^<]*<\\/(a|A|(?i)(meta))[\\s]*>";
		String encodingForLineBreaks = "1922135";
		String encodingForImageTags = "5312219";
		String articleTitle = "";
		String regExForUnnecessaryString = "(?i)(related[\\s]*|read[\\s]?(more|also))[\\s]:";
		String regExToFindStartOfTheString = "<[^>/]*>";
		String regExForEndOfTheTag = "</[^>]*>";
		Pattern patternForStartTag = Pattern
				.compile(regExToFindStartOfTheString);
		Pattern patternForAnchorRemoval = Pattern
				.compile(regexForAnchorTagRemoval);
		ArticleExtractor ce = null;
		List<String> imageUrls = new ArrayList<>();
		List<TextBlock> blocks = null;
		URL pagelink = new URL(
				"http://www.apherald.com/Politics/ViewArticle/102878/father-rented-cycles-son-is-the-minister/");
		InputSource ins = null;
		ce = CommonExtractors.ARTICLE_EXTRACTOR;
		final ImageExtractor ie = ImageExtractor.INSTANCE;

		List<Image> imageExtracted = ie.process(pagelink, ce);
		if (!(imageExtracted == null || imageExtracted.isEmpty())) {
			Collections.sort(imageExtracted);
			// observer first20chars length
			for (Image img : imageExtracted) {

				String absolutePath = img.getSrc();
				if (!StringUtils.isEmpty(absolutePath)) {
					if (!absolutePath.startsWith("http")) {
						System.out.println(pagelink.getHost() + absolutePath);
					} else {
						System.out.println(pagelink.getHost());
					}
				}

				System.out.println(img.getSrc());
			}
		} else {
			URLConnection urlconnection = pagelink.openConnection();
			InputStream inps = urlconnection.getInputStream();
			BufferedReader buffreader = new BufferedReader(
					new InputStreamReader(inps));
			String data = null;
			StringBuilder htmlDoc = new StringBuilder();
			while ((data = buffreader.readLine()) != null) {
				htmlDoc.append(data);
			}
			buffreader.close();
			String documentDownloaded = htmlDoc.toString();
			Matcher matcherForImage = patternForImageExtraction
					.matcher(documentDownloaded);
			while (matcherForImage.find()) {
				int startPos = matcherForImage.start();
				int endPos = matcherForImage.end();
				System.out.println(documentDownloaded.substring(startPos,
						endPos));
			}

		}

		final HTMLHighlighter obj = HTMLHighlighter.newHighlightingInstance();

		String result = obj.process(pagelink, ce);
		// System.out.println(obj.processHtml()); ;
		blocks = obj.getTextDocument().getTextBlocks();
		// obj.processTargetHtml(doc, is);
		System.out.println(obj.getTextDocument().getTitle());
		if (!StringUtils.isEmpty(obj.getTextDocument().getTitle())) {
			int titleindexpos = 0;
			int index = 0;
			int lastSeenMax = 0;
			int lengthofTitle = 0;
			String[] array = null;
			array = obj.getTextDocument().getTitle().split(":|-|\\|");
			while (index < array.length) {
				lengthofTitle = array[index].length();
				if (lengthofTitle > lastSeenMax) {
					lastSeenMax = lengthofTitle;
					titleindexpos = index;

				}
				index++;
			}
			System.out.println("xxxx" + array[titleindexpos]);
		}

		/*
		 * result = result.replaceAll("<BR>", ""); result =
		 * result.replaceAll("</BR>", encodingForLineBreaks); Matcher
		 * matcherForIMageTag = patternForImageExtraction.matcher(result);
		 */

		/*
		 * while (matcherForIMageTag.find()) { int startIndex =
		 * matcherForIMageTag.start(); int endIndex = matcherForIMageTag.end();
		 * imageUrls.add(result.substring(startIndex, endIndex)); result =
		 * matcherForIMageTag.replaceFirst(encodingForImageTags); }
		 */
		result = "<html><head></head><body>" + result + "</body></html>";
		result = result.replaceAll("<BR>", "");
		result = result.replaceAll("</BR>", encodingForLineBreaks);
		htmlContentStack(result, removeAnchorTags);

		/*
		 * Matcher mathcerForAnchor = patternForAnchorRemoval.matcher(result);
		 * if (mathcerForAnchor.find()) { result =
		 * mathcerForAnchor.replaceAll(""); }
		 */

	}

	public static void print(Node node, String indent) {
		Node child = node.getFirstChild();
		while (child != null) {
			print(child, indent + "");
			child = child.getNextSibling();
		}
	}

	// we are assuming that we will get well balanced tags
	public static String htmlContentStack(String htmlString,
			boolean removeAnchorTags) {
		Map<String, String> numberedTagWithContent = new LinkedHashMap<String, String>();
		List<String> tagWithWordCount = new ArrayList<String>();
		NdContentStack stack = new NdContentStack();
		int lengthOfString = htmlString.length();
		int absposition = 0;
		int begindex = 0;
		int tagnumber = 0;
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
				stack.push(tagName + ":" + tagnumber);

			}
			if (isEndofTheTag(htmlString)) {
				if (mathcerForEnd.find()) {

					int position = begindex = mathcerForEnd.start();

					int endIndex = mathcerForEnd.end();
					tagName = htmlString.substring(position, endIndex);
					absposition += tagName.length();
					htmlString = htmlString.substring(endIndex);
				}
				int pos = stack.getTop();
				String lastTag = stack.getTagInfo().get(pos);
				if (tagName.substring(2, tagName.length() - 1) == lastTag
						.substring(1, lastTag.length() - 1))
					stack.pop();
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
					int numberofWordsInContent = getWordCount(content);
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

		// getHtmlContent(numberedTagWithContent, tagWithWordCount);
		getFinalContent(numberedTagWithContent, tagWithWordCount,
				removeAnchorTags);

		return null;
	}

	public static String getHtmlContent(Map<String, String> tagContent,
			List<String> tagContentLenghts) {
		int indexOfMaximumContent = 0;
		int tempIndex = 0;
		String maximumlengthContent = null;
		String tagAndContentCount = null;
		String[] tagPositionDetails = null;
		int lastseentagPos = 0;
		int currentElementTagPos = 0;
		int tagposionofMaximumLength = 0;
		List<String> tagsContentoriginal = new ArrayList<String>(
				tagContentLenghts);
		TagCountComparator tg = new TagCountComparator();
		Collections.sort(tagContentLenghts, tg);
		if (tagContentLenghts.size() != 0) {
			tagAndContentCount = tagContentLenghts
					.get(tagContentLenghts.size() - 1);
			tagPositionDetails = tagAndContentCount.split(":");
			int maxcontentLength = Integer.parseInt(tagPositionDetails[1]);
			int contentLength = maxcontentLength;
			String tagNameAndItsPoistionCoordinates[] = tagPositionDetails[0]
					.split("-");
			maximumlengthContent = tagContent.get(tagPositionDetails[0]);
			lastseentagPos = tagposionofMaximumLength = Integer
					.parseInt(tagNameAndItsPoistionCoordinates[1]);
			// Iterator<String> it= tagsContentoriginal.iterator();
			for (int i = 0; i < tagContentLenghts.size(); i++) {
				String keyname = tagContentLenghts.get(i);
				if (keyname.equals(tagAndContentCount))
					break;
				else {
					indexOfMaximumContent++;
				}
			}
			tempIndex = indexOfMaximumContent;

			while (tempIndex > 0) {
				tempIndex--;
				contentLength = Integer.parseInt(tagPositionDetails[1]);
				tagAndContentCount = tagsContentoriginal.get(tempIndex);
				tagPositionDetails = tagAndContentCount.split(":");
				String currentContent = tagContent.get(tagPositionDetails[0]);
				tagNameAndItsPoistionCoordinates = tagPositionDetails[0]
						.split("-");
				currentElementTagPos = Integer
						.parseInt(tagNameAndItsPoistionCoordinates[1]);
				// very Important minimum length should be Considered
				if (lastseentagPos - currentElementTagPos > 4) {

					if (lastseentagPos - currentElementTagPos > 10) {
						break;
					}

					if (contentLength < Math.ceil(maxcontentLength * .3)) {
						continue;
					}

				} else {

					if (currentContent.length() > 32) {
						if (currentContent.startsWith("<a")
								|| currentContent.startsWith("<A")) {
							if (currentContent.length() > 150) {
								currentContent = currentContent.replaceFirst(
										"<A", "<p");
								currentContent = currentContent.replaceFirst(
										"</A", "</p");
							} else {
								currentContent = "";
							}
						}
						maximumlengthContent = currentContent
								+ maximumlengthContent;
						lastseentagPos = currentElementTagPos;
					}
				}

			}
			tempIndex = indexOfMaximumContent;
			lastseentagPos = tagposionofMaximumLength;
			while (tempIndex < tagContentLenghts.size() - 1) {

				tempIndex++;

				contentLength = Integer.parseInt(tagPositionDetails[1]);
				tagAndContentCount = tagsContentoriginal.get(tempIndex);
				tagPositionDetails = tagAndContentCount.split(":");
				String currentContent = tagContent.get(tagPositionDetails[0]);
				tagNameAndItsPoistionCoordinates = tagPositionDetails[0]
						.split("-");
				currentElementTagPos = Integer
						.parseInt(tagNameAndItsPoistionCoordinates[1]);

				if (Math.abs(lastseentagPos - currentElementTagPos) > 4) {

					if (lastseentagPos - currentElementTagPos > 10) {
						break;
					}

					if (contentLength < Math.ceil(maxcontentLength * .3)) {
						continue;
					}

				} else {
					maximumlengthContent = maximumlengthContent
							+ currentContent;
					lastseentagPos = currentElementTagPos;
				}

			}

		}
		// System.out.println(maximumlengthContent);

		return maximumlengthContent;
	}

	public static String getFinalContent(Map<String, String> tagContent,
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
						+ getWordCount(tagContent.get(tagNameWithIndex));
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
			tagMetaData.setWordCount(getWordCount(text.trim()));
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
		if(!StringUtils.isEmpty(metaDataForTitle))
		{
		System.out.println(" title"
				+ tagContent.get(metaDataForTitle.split(":")[0])
				+ "end of title");
		}
		System.out.println(description.toString().replaceAll("[\\s]+", " "));

		return null;
	}

	private static int getWordCount(String str) {
		if (StringUtils.isBlank(str)) {
			return 0;
		}
		return str.split("\\s+").length;
	}

	private static void enableDescriptionFlag(int currentIndex,
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

	private static boolean toleranceCheck(int currentIndex,
			List<TagMetaData> tagMetaDatas, int toleranceCount,
			boolean traverseForward) {
		int index = 0;
		int skipCount = 0;

		if (traverseForward) {
			while (true) {
				if (tagMetaDatas.get(currentIndex - index)
						.isPartOfDescription() == false) {
					skipCount++;
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
					skipCount++;
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

	public static boolean isBeginningOfTag(String remainingString) {
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

	public static String searchTitleMetaData(List<String> tagStructure) {
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
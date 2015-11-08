package com.kohlschutter.boilerpipe.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ContentParser {

	static String regExToFindStartOfTheString = "^<[^>/]*>";
	static String regExForEndOfTheTag = "^</[^>]*>";
	static String delimiterBeforeOpenAndClosingTags = "<";
	static Pattern patternForStartTag = Pattern
			.compile(regExToFindStartOfTheString);
	static Pattern patternForEndTag = Pattern.compile(regExForEndOfTheTag);
	static Pattern patternForBeginningHtmlElement = Pattern
			.compile(delimiterBeforeOpenAndClosingTags);

	public static void main(String[] args) {
		// TODO Autbodo-generated method stub
		String htmlDocument = "<html><head>sridhar</head><body>this is the real text to be displayed</body></html>";
		htmlContentStack(htmlDocument);
	}

	public static String htmlContentStack(String htmlString) {
		Map<String, String> numberedTagWithContent = new LinkedHashMap<String, String>();
		Map<String, Integer> numberedTagWithTokenCount = new LinkedHashMap<String, Integer>();
		List<String> tagWithWordCount = new ArrayList<String>();
		NdContentStack stack = new NdContentStack();
		int lengthOfString = htmlString.length();
		int absposition = 0;
		int begindex = 0;
		// int endIndex=0;
		int tagnumber = 0;
		String tagName = null;
		Matcher matcherForStart = patternForStartTag.matcher(htmlString);

		while (absposition < lengthOfString - 4) {
			/*
			 * int maxBoundary = Math.min(htmlString.substring(absposition)
			 * .length(), 50);
			 */
			String startString = htmlString;
			System.out.println(htmlString);
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
					// tagName = tagName + ":" + tagnumber;
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
				Matcher mathcerForDelimiterForOpenAndClosingElement = patternForBeginningHtmlElement
						.matcher(htmlString);
				if (mathcerForDelimiterForOpenAndClosingElement.find()) {
					int endIndex = mathcerForDelimiterForOpenAndClosingElement
							.start();
					String content = htmlString.substring(0, endIndex);
					int numberofWordsInContent = content.length();
					htmlString = htmlString.substring(endIndex);
					content = tagName + content + "</"
							+ tagName.substring(1, tagName.length());
					numberedTagWithContent.put(tagName + "-" + tagnumber,
							content);
					/*
					 * numberedTagWithTokenCount.put(tagName + "-" +
					 * tagnumber+":"+numberofWordsInContent,
					 * numberofWordsInContent);
					 */
					tagWithWordCount.add(tagName + "-" + tagnumber + ":"
							+ numberofWordsInContent);
					absposition += endIndex;
				}
			}
		}

		getHtmlContent(numberedTagWithContent, tagWithWordCount);

		return null;
	}

	public static String getHtmlContent(Map<String, String> tagContent,
			List<String> tagContentLenghts) {
		int indexOfMaximumContent = 0;
		int tempIndex = 0;
		String maximumlengthContent=null;
		String tagAndContentCount = null;
		String[] tagPositionDetails = null;
		int lastseentagPos = 0;
		int currentElementTagPos = 0;
		int tagposionofMaximumLength = 0;
		List<String> tagsContentoriginal=new ArrayList<String>(tagContentLenghts); 
		//Collections.copy(tagsContentoriginal,tagContentLenghts); /*tagContentLenghts.subList(0,
			//	tagContentLenghts.size());*/
		TagCountComparator tg = new TagCountComparator();
		System.out.println("-------------------------xxxxxxxxxxxx---------------------");
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
		//Iterator<String> it=	tagsContentoriginal.iterator();
			for(int i=0;i<tagContentLenghts.size();i++) {
				String keyname=tagContentLenghts.get(i);
				if (keyname.equals(tagAndContentCount))
					break;
				else {
					indexOfMaximumContent++;
				}
			}
			tempIndex = indexOfMaximumContent;

			while (tempIndex > 0) {
				// maximumlengthContent

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
					maximumlengthContent = currentContent
							+ maximumlengthContent;
					lastseentagPos = currentElementTagPos;
				}

			}
			tempIndex = indexOfMaximumContent;
			lastseentagPos = tagposionofMaximumLength;
			while (tempIndex < tagContentLenghts.size()-1) {

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
		System.out.println(maximumlengthContent);

		return maximumlengthContent;
	}

	public static boolean isBeginningOfTag(String remainingString) {
		if (!StringUtils.isEmpty(remainingString)) {
			Matcher matcherForStartOfTheTag = patternForStartTag
					.matcher(remainingString);
			boolean truthval = matcherForStartOfTheTag.find();
			System.out.println(truthval);
			return truthval;
		} else {
			return false;
		}
	}

	public static boolean isEndofTheTag(String remainingString) {
		if (!StringUtils.isEmpty(remainingString)) {
			Matcher mathcerForEnd = patternForEndTag.matcher(remainingString);
			boolean truthval = mathcerForEnd.find();
			System.out.println(truthval);
			return truthval;
		} else {
			return false;
		}
		// return false;
	}

	public static boolean isContent(String remainingString, int pos) {

		return false;
	}

}

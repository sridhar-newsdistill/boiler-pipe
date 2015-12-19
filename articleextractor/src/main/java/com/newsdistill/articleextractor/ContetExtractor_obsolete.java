package com.newsdistill.articleextractor;

import java.util.List;

public class ContetExtractor_obsolete {
	/*private String getFinalContent(Map<String, String> tagContent,
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
	
	 * System.out.println(" title" +
	 * tagContent.get(metaDataForTitle.split(":")[0]) + "end of title");
	 
}
// System.out.println(description.toString().replaceAll("[\\s]+", " "));

return description.toString().replaceAll("[\\s]+", " ");
}*/
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
	/*	private String getCleanedDescription(String htmlString) {

	Map<String, String> numberedTagWithContent = new LinkedHashMap<String, String>();
	List<String> tagWithWordCount = new ArrayList<String>();
	int absposition = 0;
	int tagnumber = 0;

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
				int position = matcherForStart.start();
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

				int position = mathcerForEnd.start();

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
}*/
	/*private boolean isBeginningOfTag(String remainingString) {

	if (!StringUtils.isEmpty(remainingString)) {

		Matcher matcherForStartOfTheTag = patternForStartTag

		.matcher(remainingString);

		boolean truthval = matcherForStartOfTheTag.find();

		return truthval;

	} else {

		return false;
	}
}*/
	public static boolean isContent(String remainingString, int pos) {

		return false;
	}
	/*private String getImage(Map<String, String> imageMap) {
	if(imageMap!=null){
	Set<String> keys = imageMap.keySet();
	Set<String> imageUrls = new LinkedHashSet<String>();
	for (String key : keys) {
		String imageData = imageMap.get(key);
		List<String> imgAttrs = Utils.getMatchedStringsForGivenText(
				ApplicationConstants.PATTERN_FOR_ATTR_DATA, imageData);
		String imageUrl = null;
		for (String imageAttribute : imgAttrs) {

			Matcher matcherForTextInAttr = PAT_FOR_TEXT_IN_ATTR
					.matcher(imageAttribute);
			int startIndx = 0;
			int endIndx = imageAttribute.length()-1;
			if (matcherForTextInAttr.find()) {
				startIndx = matcherForTextInAttr.start();
				//endIndx = matcherForTextInAttr.end();
			}
     if(startIndx == endIndx)
      {
    	return null;
      }
			imageUrl = imageAttribute.substring(startIndx + 2, endIndx);
			if (!imageUrl.startsWith("http")) {
				imageUrl = Utils.getAbsoluteUrl(this.Url, imageUrl);
			}
			imageUrls.add(imageUrl);
		}
	}
	return getMainImage(imageUrls);
	}
	else{
		return null;
	}
}*/
	
}

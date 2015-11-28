package com.newsdistill.articleextractor;

public class TagMetaData {
	Integer tagCount;

	String tagName;

	String tagNameWithIndex;

	Integer wordCount;

	String text;

	int index;

	boolean isPartOfDescription;

	public TagMetaData(String metadata) {
		wordCount = Integer.parseInt((metadata.split(ApplicationConstants.TAG_CONTENT_WORDCNT_DELIM)[1]).trim());
		tagNameWithIndex = (metadata.split(ApplicationConstants.TAG_CONTENT_WORDCNT_DELIM)[0]).trim();
		String tagArray[] = tagNameWithIndex.split(ApplicationConstants.TAG_NAME_TAGNUM_DELIM);
		System.out.println(tagArray[0]);
		System.out.println(tagArray[1]);
		          
		//tagName = ((metadata.split(ApplicationConstants.TAG_CONTENT_WORDCNT_DELIM)[0]).split(ApplicationConstants.TAG_NAME_TAGNUM_DELIM)[0]).trim();
		tagCount = Integer.parseInt(((tagNameWithIndex.split(ApplicationConstants.TAG_NAME_TAGNUM_DELIM)[1]).trim()));

	}

	public Integer getTagCount() {
		return tagCount;
	}

	public void setTagCount(Integer tagCount) {
		this.tagCount = tagCount;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Integer getWordCount() {
		return wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTagNameWithIndex() {
		return tagNameWithIndex;
	}

	public void setTagNameWithIndex(String tagNameWithIndex) {
		this.tagNameWithIndex = tagNameWithIndex;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isPartOfDescription() {
		return isPartOfDescription;
	}

	public void setPartOfDescription(boolean isPartOfDescription) {
		this.isPartOfDescription = isPartOfDescription;
	}

}

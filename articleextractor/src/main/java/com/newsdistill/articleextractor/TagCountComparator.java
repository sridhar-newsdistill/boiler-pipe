package com.newsdistill.articleextractor;

import java.util.Comparator;


public class TagCountComparator implements Comparator<String> {
	public int compare(String value1, String value2) {
		// TODO Auto-generated method stub

		String[] array1 = value1.split(ApplicationConstants.TAG_CONTENT_WORDCNT_DELIM);
		String[] array2 = value2.split(ApplicationConstants.TAG_CONTENT_WORDCNT_DELIM);
		int length1 = Integer.parseInt(array1[1]);
		int length2 = Integer.parseInt(array2[1]);
		int comparisonStatus = (length1 > length2) ? -1
				: (length1 == length2) ? 0 : 1;
		return comparisonStatus;
	}

}

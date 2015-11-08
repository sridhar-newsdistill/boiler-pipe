package com.newsdistill.articleextractor.utils;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	public static int getWordCount(String str) {
		if (StringUtils.isBlank(str)) {
			return 0;
		}
		return str.split("\\s+").length;
	}
	
}

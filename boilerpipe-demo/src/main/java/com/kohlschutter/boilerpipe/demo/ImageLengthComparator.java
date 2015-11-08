package com.kohlschutter.boilerpipe.demo;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

public class ImageLengthComparator implements Comparator<String> {

	@Override
	public int compare(String img1, String img2) {

		if (StringUtils.isEmpty(img1) || StringUtils.isEmpty(img2)) {
		return	(StringUtils.isEmpty(img1))?-1:1;	
		}
		return img1.length() > img2.length() ? 1 : img1.length() == img2
				.length() ? 0 : -1;
	}

}

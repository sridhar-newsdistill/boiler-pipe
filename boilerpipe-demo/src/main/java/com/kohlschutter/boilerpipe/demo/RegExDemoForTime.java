package com.kohlschutter.boilerpipe.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExDemoForTime {

	public static void main(String[] args) {
		 String regexForhhmmss="(((0)?[0-9]|1[0-9]|2[0-3]):([0-5][0-9])(:([0-5][0-9])(\\.[0-9]*)?)?)";
		// TODO Auto-generated method stub
		 String dateValue="Wed 04 Nov 04:20:33.4870372015";
		 Pattern patternTobeTested=Pattern.compile(regexForhhmmss);
	Matcher matcher=	 patternTobeTested.matcher(dateValue);
		 while(matcher.find())
		 {
			 System.out.println(dateValue.substring(matcher.start()));
		 }
	}

}

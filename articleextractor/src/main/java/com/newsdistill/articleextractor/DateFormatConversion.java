package com.newsdistill.articleextractor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatConversion {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date cal= Calendar.getInstance().getTime();
		String date=sdf.format(cal);
		System.out.println(date);
	}

}

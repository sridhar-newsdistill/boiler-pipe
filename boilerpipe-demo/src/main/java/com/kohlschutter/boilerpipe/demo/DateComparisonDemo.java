package com.kohlschutter.boilerpipe.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateComparisonDemo {

	public static void main(String[] args) throws ParseException {
		String date1 = "12-10-2015 00:00:00";
		String date2 = "13-10-2015 00:00:00";
		String date3 = "13-10-2015 00:00:00";
		List<Date> datesLits = new ArrayList<Date>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		datesLits.add(sdf.parse(date1));
		datesLits.add(sdf.parse(date2));
		datesLits.add(sdf.parse(date3));
		Collections.sort(datesLits);
		for(Date dt:datesLits)
		{
			System.out.println(dt.toString());
		}
	}

}

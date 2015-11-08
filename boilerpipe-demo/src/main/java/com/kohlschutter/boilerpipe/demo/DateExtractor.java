package com.kohlschutter.boilerpipe.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
//import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateExtractor {

	static Map<String, Month> data = new LinkedHashMap<String, Month>();
	static{
		data.put("jan", Month.JANUARY);
		data.put("feb", Month.FEBRUARY);
		data.put("march", Month.MARCH);
		data.put("april", Month.APRIL);
		data.put("may", Month.MAY);
		data.put("jun", Month.JUNE);
		data.put("jul", Month.JULY);
		data.put("aug", Month.AUGUST);
		data.put("sep", Month.SEPTEMBER);
		data.put("oct", Month.OCTOBER);
		data.put("nov", Month.NOVEMBER);
		data.put("dec", Month.DECEMBER);

	}
	static String regexForYear = "[^\\d\\w](19|20|21)[0-9]{2}([^\\d\\w])?";
	static String regexForMonthName = "(?i)(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)";
	static String regExForDate = "(0)?[1-9]|1[0-9]|2[0-9]|3[0-1]";
	static String regexForDate = "(0)?[1-9]|1[012]";
	static String regexForhhmmss = "(((0)?[0-9]|1[0-9]|2[0-3]):([0-5][0-9])(:([0-5][0-9])(\\.[0-9]*)?)?)";
	static String regexForAmPm = "(?i)(am|pm)";
	static Pattern patternForAmPmInfo = Pattern.compile(regexForAmPm);
	static Pattern patternForHourMinuteInfo = Pattern.compile(regexForhhmmss);
	static Pattern patternForDate = Pattern.compile(regexForDate);
	static Pattern patternForMonthName = Pattern.compile(regexForMonthName);
	static Pattern patternforYearInDate = Pattern.compile(regexForYear);

	// static Pattern patter
	public static void main(String[] args) throws IOException {
		List<String> datesIdentified = new ArrayList<String>();
		InputStream ins = null;
		SimpleDateFormat targetDateFormatwithAmPm = new SimpleDateFormat(
				"dd-MM-YYYY hh:mm:ss a");
		SimpleDateFormat targetDateFormatWithOnlyAm = new SimpleDateFormat(
				"dd-MM-YYYY HH:mm:ss");
		int lastReadCharPos = 0;
		Calendar cal = Calendar.getInstance();
		LocalDateTime localDate = LocalDateTime.now();
		URL pagelink = new URL(
				"http://timesofindia.indiatimes.com/india/PM-Narendra-Modi-is-ninth-most-powerful-figure-in-Forbes-list/articleshow/49661793.cms");
		URLConnection urlconnet = pagelink.openConnection();
		ins = urlconnet.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
		String dataread = null;

		StringBuilder sb = new StringBuilder();
		while ((dataread = br.readLine()) != null) {
			sb.append(dataread);
		}
		dataread = sb.toString();
		ZoneId zoneInfo = ZoneId.of("UTC");
		// System.out.println(localDate.now());
		System.out.println(localDate.now(zoneInfo));

		// long timeValue=date.getTime();

		Matcher mathcerForHHMM = patternForHourMinuteInfo.matcher(dataread);
		while (mathcerForHHMM.find()) {
			String hourInfo = null;
			String dateInfo = null;
			String monthInfo = null;
			String yearInfo = null;
			boolean hasAmPmInfo = false;
			String subString = null;
			int startIndex = mathcerForHHMM.start();
			int endIndex = mathcerForHHMM.end();
			// check hh:mm:ss info
			if (startIndex - 25 >= 0) {
				int tempStartIndex = startIndex - 40;
				int tempEndIndex = endIndex + 30;
				// check am/pm exist
				boolean isMonthInfoFound = false;
				Matcher AmPmmatcher = patternForAmPmInfo.matcher(dataread
						.substring(endIndex, endIndex + 5));
				hasAmPmInfo = AmPmmatcher.find();

				Matcher matcherForYear = patternforYearInDate
						.matcher(subString);

				// check before and after
				// check year comes before hour minute info before checking
				while (matcherForYear.find()) {

				}
				subString = dataread.substring(tempStartIndex, startIndex);

				Matcher matcherForMonth = patternForMonthName.matcher(dataread
						.substring(tempStartIndex, startIndex));

				if (matcherForMonth.find()) {
					isMonthInfoFound = true;

				}

				System.out
						.println(dataread.substring(tempStartIndex, endIndex));
				subString = dataread.substring(tempStartIndex, startIndex);

				if (mathcerForHHMM.find()) {

				}

				// if yes check for numeric month and date/ numeric date alpha
				// numeric month

				// if u dont' have any hh:mm:ss
				// check if there exists month or not

			}
			if (endIndex + 30 < dataread.length()) {
			}
		}
	}

}

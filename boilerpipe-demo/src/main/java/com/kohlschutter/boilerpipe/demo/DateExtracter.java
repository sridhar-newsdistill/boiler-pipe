package com.kohlschutter.boilerpipe.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class DateExtracter {
	public final static Map<String, Integer> monthsdata = new LinkedHashMap<String, Integer>();
	static {
		monthsdata.put("jan", 1);
		monthsdata.put("feb", 2);
		monthsdata.put("march", 3);
		monthsdata.put("april", 4);
		monthsdata.put("may", 5);
		monthsdata.put("jun", 6);
		monthsdata.put("jul", 7);
		monthsdata.put("aug", 8);
		monthsdata.put("sep", 9);
		monthsdata.put("oct", 10);
		monthsdata.put("nov", 11);
		monthsdata.put("dec", 12);
	}
	static String regexForYear = "(19|20|21)[0-9]{2}([^\\d\\w])?";
	static String regexForMonthName = "(?i)(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)";
	static String regExForDate = "1[0-9]|2[0-9]|3[0-1]|(0)?[1-9]";
	static String regExForTimeZones = "IST|UTC|GMT|CET|EET|GB|MET|NZ|PRC|ROK|UCT|WET|EST|HST|MST|ACT|AET|AGT|ART|AST|BET|BST|CAT|CNT|CST|CTT|EAT|ECT|IET|JST|MIT|NET|NST|PLT|PNT|PRT|PST|SST|VST";
	static String regexForDateWithNonDigitCharactes = "[1-2][0-9]|3[0-1]|(0)?[1-9](th|st|nd|rd|[^\\d\\w])?";
	static String regexForhhmmss = "((1[0-9]|2[0-3]|(0)?[0-9]):([0-5][0-9])(:([0-5][0-9])(\\.[0-9]*)?)?)";
	static String regexForAmPm = "(?i)(am|pm)";
	static String regexForNumericDateFormat = "[0-9-/]{10}";
	static Pattern patternForZoneIdentification = Pattern
			.compile(regExForTimeZones);
	static Pattern patternForNumericDateFormat = Pattern
			.compile(regexForNumericDateFormat);
	static Pattern patternForDateWithNonDigitCharactes = Pattern
			.compile(regexForDateWithNonDigitCharactes);
	static Pattern patternForAmPmInfo = Pattern.compile(regexForAmPm);
	static Pattern patternForHourMinuteInfo = Pattern.compile(regexForhhmmss);
	static Pattern patternForDate = Pattern.compile(regExForDate);
	static Pattern patternForMonthName = Pattern.compile(regexForMonthName);
	static Pattern patternforYearInDate = Pattern.compile(regexForYear);

	public static void main(String[] args) throws IOException {

		int begIndexForYear = 0;
		int endIndexForYear = 0;
		int begIndexForMonth = 0;
		int endIndexForMonth = 0;
		int begIndexForDate = 0;
		int endIndexForDate = 0;
		int begIndexForHourInfo = 0;
		int endIndexForHourInfo = 0;
		int endIndexForAmPm = 0;
		Integer numericYear = null;
		Integer numericMonth = null;
		Integer numericDate = null;
		boolean ampmInformation = false;
		Calendar cal = Calendar.getInstance();
		numericYear = cal.get(Calendar.YEAR);
		numericMonth = cal.get(Calendar.MONTH)+1;
		
		numericDate = cal.get(Calendar.DATE);
		String defaultZone = "";
		String defalutyear = numericYear.toString();
		String defalutDate = numericDate.toString().length() == 1 ? ("0" + numericDate
				.toString()) : "0" + numericDate.toString();
		String defalutmonth = numericMonth.toString();
		String defaluthourMinutes = "00:00:00";
		String htmlDocument = "";
		String amPmInfo = "";
		htmlDocument = "Sakshi | Updated: November 07th, 2015 18:02 (IST)";
		if (isPageContainHourMinuteInfo(htmlDocument)) {

			Matcher matcherForHourMinuteInfo = patternForHourMinuteInfo
					.matcher(htmlDocument);
			if (matcherForHourMinuteInfo.find()) {
				begIndexForHourInfo = matcherForHourMinuteInfo.start();
				int endIndexForHourInfoTemp = matcherForHourMinuteInfo.end();
				if (endIndexForHourInfo == 0) {
					defaluthourMinutes = htmlDocument.substring(
							begIndexForHourInfo, endIndexForHourInfoTemp);
					endIndexForHourInfo = endIndexForHourInfoTemp;
				}
			}
			String timeZoneRegion = htmlDocument.substring(endIndexForHourInfo,
					Math.min(endIndexForHourInfo + 20, htmlDocument.length()));
			Matcher matcherForZoneIdentification = patternForZoneIdentification
					.matcher(timeZoneRegion);
			if (matcherForZoneIdentification.find()) {
				int zoneInfoBeginIndex = matcherForZoneIdentification.start();
				int zoneInfoEndIndex = matcherForZoneIdentification.end();
				defaultZone = timeZoneRegion.substring(zoneInfoBeginIndex,
						zoneInfoEndIndex);
			}
			String amPmDataregion = htmlDocument.substring(endIndexForHourInfo,
					Math.min(endIndexForHourInfo + 5, htmlDocument.length()));
			Matcher matcherForAmPm = patternForAmPmInfo.matcher(htmlDocument
					.substring(
							endIndexForHourInfo,
							Math.min(endIndexForHourInfo + 5,
									htmlDocument.length())));
			ampmInformation = matcherForAmPm.find();
			if (ampmInformation) {
				int startIndexForAmPm = matcherForAmPm.start();
				int endIndexForAmPmtemp = matcherForAmPm.end();
				if (endIndexForAmPm == 0) {
					amPmInfo = amPmDataregion.substring(startIndexForAmPm,
							endIndexForAmPmtemp);
					endIndexForAmPm = endIndexForAmPmtemp;
				}
			}

			if (defaluthourMinutes.length() != 8) {
				if (defaluthourMinutes.length() > 8) {
					defaluthourMinutes = defaluthourMinutes.substring(0, 8);
				} else {
					if (defaluthourMinutes.length() == 6) {
						defaluthourMinutes = defaluthourMinutes + "00";
					} else if (defaluthourMinutes.length() == 5) {
						defaluthourMinutes = defaluthourMinutes + ":00";
					} else {
						defaluthourMinutes = defaluthourMinutes + "0";
					}
				}
			}

			String pretextContainingInformation = htmlDocument.substring(
					Math.max(begIndexForHourInfo - 30, 0), begIndexForHourInfo);

			Matcher mathcerForNumericDate = patternForDateWithNonDigitCharactes
					.matcher(pretextContainingInformation);
			if (mathcerForNumericDate.find()) {
				int begDateInfo = mathcerForNumericDate.start();
				int endDateInfotemp = mathcerForNumericDate.end();
				if (endIndexForDate == 0) {
					defalutDate = pretextContainingInformation.substring(
							begDateInfo, begDateInfo + 2);
					defalutDate = defalutDate.replaceAll("[\\D]", "");
					endIndexForDate = endDateInfotemp;
				}
				Matcher matcherForMonth = patternForMonthName
						.matcher(pretextContainingInformation);
				if (matcherForMonth.find()) {
					begIndexForMonth = matcherForMonth.start();
					int endIndexForMonthtemp = matcherForMonth.end();
					if (endIndexForMonth == 0) {
						String monthInfo = pretextContainingInformation
								.substring(begIndexForMonth,
										endIndexForMonthtemp);
						numericMonth = monthsdata.get(monthInfo.substring(0, 3)
								.toLowerCase());
						endIndexForMonth = endIndexForMonthtemp;
					}
				}

				String dateObject = defalutDate + "-" + numericMonth + "-"
						+ numericYear + "~" + defaluthourMinutes + "~"
						+ amPmInfo;
				constructDateFromDateObject(dateObject, ampmInformation);
			} else {
				/*
				 * pretextContainingInformation = pretextContainingInformation
				 * .replaceFirst(defalutyear, "");
				 */
				Matcher matcherForMonthName = patternForMonthName
						.matcher(pretextContainingInformation);
				if (matcherForMonthName.find()) {
					begIndexForMonth = matcherForMonthName.start();
					endIndexForMonth = matcherForMonthName.end();
					String monthName = pretextContainingInformation.substring(
							begIndexForMonth, endIndexForMonth);
					numericMonth = monthsdata.get(monthName.toLowerCase());
					String textBetweenMonthAndHours = pretextContainingInformation
							.substring(endIndexForMonth, begIndexForHourInfo);
					// textBetweenMonthAndHours.
					Matcher matcherForDate = patternForDateWithNonDigitCharactes
							.matcher(textBetweenMonthAndHours);
					if (matcherForDate.find()) {
						begIndexForDate = matcherForDate.start();
						endIndexForDate = matcherForDate.end();
						defalutDate = textBetweenMonthAndHours.substring(
								begIndexForDate, endIndexForDate).substring(0,
								2);
					} else {
						String remainingStrigForDateLookUp = pretextContainingInformation
								.substring(0, begIndexForMonth);
						matcherForDate = patternForDateWithNonDigitCharactes
								.matcher(remainingStrigForDateLookUp);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							defalutDate = remainingStrigForDateLookUp
									.substring(begIndexForDate, endIndexForDate)
									.substring(0, 2);
						}
					}

				}
			}

		} else if (isPageContainsCurrentYearInfo(htmlDocument)) {
			Matcher matcherForYear = patternforYearInDate.matcher(htmlDocument);
			if (matcherForYear.find()) {
				begIndexForYear = matcherForYear.start();
				endIndexForYear = matcherForYear.end();
				defalutyear = htmlDocument.substring(begIndexForYear,
						endIndexForYear);
				String textToBeSearched = htmlDocument.replace(defalutyear, "");

				Matcher matcherForMonth = patternForMonthName
						.matcher(textToBeSearched);
				if (matcherForMonth.find()) {
					begIndexForMonth = matcherForMonth.start();
					int endIndexForMonthtemp = matcherForMonth.end();
					if (endIndexForMonth == 0) {
						String monthInfo = textToBeSearched.substring(
								begIndexForMonth, endIndexForMonthtemp);
						numericMonth = monthsdata.get(monthInfo.substring(0, 3)
								.toLowerCase());
						endIndexForMonth = endIndexForMonthtemp;
						// once you identify month now look for date
						String postTextMayContainDateInfo = textToBeSearched
								.substring(endIndexForMonth, Math.min(
										endIndexForMonth + 5,
										textToBeSearched.length()));
						String pretextmayContainDateInfo = textToBeSearched
								.substring(Math.max(begIndexForMonth - 5, 0),
										begIndexForMonth);
						Matcher matcherForDate = patternForDateWithNonDigitCharactes
								.matcher(pretextmayContainDateInfo);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							String textContaningDate = pretextmayContainDateInfo
									.substring(begIndexForDate, endIndexForDate);
							defalutDate = textContaningDate.replaceAll("\\D",
									"");
						} else {
							matcherForDate = patternForDateWithNonDigitCharactes
									.matcher(postTextMayContainDateInfo);
							if(matcherForDate.find())
							{
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							String textContaningDate = postTextMayContainDateInfo
									.substring(begIndexForDate, endIndexForDate);
							defalutDate = textContaningDate.replaceAll("\\D",
									"");
							}
						}

					}
				} else {
					Matcher matcherForDate = patternForDateWithNonDigitCharactes
							.matcher(textToBeSearched);
					if(matcherForDate.find())
					{
					begIndexForDate = matcherForDate.start();
					endIndexForDate = matcherForDate.end();
					String textContaningDate = textToBeSearched.substring(
							begIndexForDate, endIndexForDate);
					defalutDate = textContaningDate.replaceAll("\\D", "");
					defalutDate = defalutDate.replaceAll("\\D", "");
					defalutDate = defalutDate.substring(0,
							Math.min(defalutDate.length(), 2));
					}
				}

				if (defalutyear.length() > 4) {
					defalutyear = defalutyear.substring(0, 4);

				}
			}
		}
		String dateObject = defalutDate + "-" + numericMonth + "-"
				+ numericYear + "~" + defaluthourMinutes + "~" + amPmInfo;
		constructDateFromDateObject(dateObject, ampmInformation);
	}

	// this downloads the page in to local machine the identifies date From the
	// downloaded Text
	public static String getDataFromPage(String url) throws IOException {
		InputStream inst = null;
		URL pagelink = new URL(url);
		URLConnection urlconnetion = pagelink.openConnection();
		inst = urlconnetion.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(inst));
		String dataread = null;

		StringBuilder sb = new StringBuilder();
		while ((dataread = br.readLine()) != null) {
			sb.append(dataread);
		}
		return sb.toString();
	}

	// this method will check whether we have is hours and minutes information

	public static boolean isPageContainHourMinuteInfo(String htmldata) {
		Matcher matcherForHourMinute = patternForHourMinuteInfo
				.matcher(htmldata);
		return matcherForHourMinute.find();
	}

	public static boolean isPageContainsCurrentYearInfo(String htmldata) {
		Matcher matcherForYear = patternforYearInDate.matcher(htmldata);
		return matcherForYear.find();
	}

	// this method constructs java date object from the given String object
	public static Date constructDateFromDateObject(String dataToBeConverted,
			boolean ampmInfo) {
		boolean isAmPmInfoAvailable = ampmInfo;
		Date dateTobeReturned = null;
		String dateFormat = "dd-MM-yyyy ";
		String hourInfo = "hh:mm:ss a";
		if (!isAmPmInfoAvailable) {
			hourInfo = "HH:mm:ss";
		}
		if (dataToBeConverted != null && dataToBeConverted.contains("~")) {
			String datevalue[] = dataToBeConverted.split("~");
			Calendar calender = Calendar.getInstance();
			Integer currentYearInfo = calender.get(Calendar.YEAR);
			if (datevalue[1].startsWith(currentYearInfo.toString())) {
				datevalue[1] = StringUtils.reverse(datevalue[1]);
			}
			dataToBeConverted = datevalue[0] + " " + datevalue[1];
			if (ampmInfo) {
				dataToBeConverted = dataToBeConverted + " " + datevalue[2];
			}
			SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat
					+ hourInfo);
			try {
				dateTobeReturned = dateFormatter.parse(dataToBeConverted);
				System.out.println(dateTobeReturned.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (dateTobeReturned == null) {
			Calendar calender = Calendar.getInstance();
			calender.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateTobeReturned = calender.getTime();
		}

		return dateTobeReturned;
	}

	// this method will construct date Object from the given string it does n't
	// help us in getting timestamp information
	public static Date getDateFromYearDataWithOutHourInfo(String date) {
		String dateFormatInfo = "dd-MM-YYYY";
		Date datetoBeReturned = null;
		SimpleDateFormat dateFormatParser = new SimpleDateFormat(dateFormatInfo);
		try {
			datetoBeReturned = dateFormatParser.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (datetoBeReturned == null) {
			Calendar calender = Calendar.getInstance();
			calender.setTimeZone(TimeZone.getTimeZone("GMT"));
			calender.add(Calendar.MINUTE, -30);
			datetoBeReturned = calender.getTime();
		}
		return datetoBeReturned;
	}

}

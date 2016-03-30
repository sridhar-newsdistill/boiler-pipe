package com.kohlschutter.boilerpipe.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	static String regexForNumericYearDateFormat = "[\\d]{1,2}[\\W\\D][\\d]{1,2}[\\W][\\d]{4}|[\\d]{4}[\\W][\\d]{1,2}[\\W][\\d]{1,2}";
	static String regexForNumericDateFormat = "[0-9-/]{10}";
	static String dateFilterTags = ".(\\*date\\*|pub\\*|\\*info\\*|\\*time\\*|\\*calendar\\*)";
	static String regexForSelectiontags = "[class~=(?i)(.*Pub.*|.*date.*|.*info.*|.*time.*|.*calendar.*|.*post.*)]";
	static Pattern patternForZoneIdentification = Pattern
			.compile(regExForTimeZones);
	static Pattern patternForNumericYearDateFormat = Pattern
			.compile(regexForNumericYearDateFormat);
	static Pattern patternForNumericDateFormat = Pattern
			.compile(regexForNumericDateFormat);
	static Pattern patternForDateWithNonDigitCharactes = Pattern
			.compile(regexForDateWithNonDigitCharactes);
	static Pattern patternForAmPmInfo = Pattern.compile(regexForAmPm);
	static Pattern patternForHourMinuteInfo = Pattern.compile(regexForhhmmss);
	static Pattern patternForDate = Pattern.compile(regExForDate);
	static Pattern patternForMonthName = Pattern.compile(regexForMonthName);
	static Pattern patternforYearInDate = Pattern.compile(regexForYear);
	static Pattern patternval = Pattern.compile(dateFilterTags);

	public static void main(String args[]) throws IOException {
		Document htmlDoc = null;
		String url = "http://www.sakshi.com/news/movies";
		Connection connection = Jsoup.connect(url);
		htmlDoc = connection.get();
		Date documentDate = dateIdentifierBlock(htmlDoc);
		System.out.println("date returned is" + documentDate);
	}

	public static Date dateIdentifierBlock(Document doc) {
		Elements elems = null;
		// Elements removedScript = null;
		int dateindex = 0;
		// Set<String> setInfo = new LinkedHashSet<String>();
		Map<String, Integer> mapForDate = new LinkedHashMap<String, Integer>();
		elems = doc.getAllElements();
		Elements allSelectedElements = elems.clone();
		allSelectedElements = allSelectedElements.select(regexForSelectiontags);
		if(allSelectedElements!=null&&allSelectedElements.size()>0)
		{
		for (Element e : allSelectedElements) {

			if (e.text().toLowerCase().contains("2015")
					&& e.text().length() < 100) {
				dateCountMap(mapForDate, e.text());
			}
		}
		}
	
		getDatesinfoAvailableInAttributes(elems, mapForDate);
		
		return extractDateFromIdentifiedInfo(dateindex, mapForDate);
	}

	private static Date extractDateFromIdentifiedInfo(int dateindex,
			Map<String, Integer> mapForDate) {
		List<Date> datesidentified = new ArrayList<Date>();
		Set<String> dates = mapForDate.keySet();
		if (dates.size() <= 0) {

            Calendar calandar = Calendar.getInstance();
            calandar.add(Calendar.HOUR, -2);
            
			return calandar.getTime();
		} else if (dates.size() == 1) {

			Object[] dateInfo = dates.toArray();

			Date dateval = dateCleanIngBlock((String) dateInfo[0]);

			if (isValidDate(dateval)) {

				return dateval;

			} else {
                       Calendar calandar = Calendar.getInstance();
                       calandar.add(Calendar.HOUR, -2);
                       dateval=calandar.getTime();
				return dateval;
			}

		} else {

			for (String datestring : dates) {

				try {

					datesidentified.add(dateCleanIngBlock(datestring));

				} catch (Exception e) {
                     e.printStackTrace();
				}
			}
		}
		Collections.sort(datesidentified);

		Collections.reverse(datesidentified);
		for (Date dateobj : datesidentified) {
			System.out.println(dateobj.toString());
		}

		while (!isValidDate(datesidentified.get(dateindex))) {

			dateindex++;
		}

		return datesidentified.get(dateindex);
	}

	private static void getDatesinfoAvailableInAttributes(Elements elems,
			Map<String, Integer> mapForDate) {
		for (Element block : elems) {
			Attributes node = block.attributes();
			Iterator<Attribute> it = node.iterator();
			while (it.hasNext()) {
				Attribute attr = it.next();
				String val = attr.getValue();
				val = val.toLowerCase();
				Matcher matcher = patternval.matcher(val);

				if ((matcher.find() || !StringUtils.isEmpty(val))
						&& (val.toLowerCase().contains("date") || val
								.toLowerCase().contains("modified"))) {
					String probableDate = block.attr("content");
					if (!StringUtils.isEmpty(probableDate)
							&& (probableDate.toLowerCase().contains("2015") && probableDate
									.length() < 100)) {

						dateCountMap(mapForDate, probableDate);
						// break;
					}
				}

			}
		}
	}

	private static void dateCountMap(Map<String, Integer> mapForDate,
			String dateInfoString) {

		Integer count = 0;

		if (!mapForDate.containsKey(dateInfoString)) {

			count = 1;

		} else {

			count = mapForDate.get(dateInfoString);

			count++;
		}

		mapForDate.put(dateInfoString, count);
	}

	private static boolean isValidDate(Date dateToBeChecked) {

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MINUTE, -10);

		Date currentDate = calendar.getTime();

		// System.out.println();

		// checking that date must be less than current time stamp
		boolean statusTobeReturned = (currentDate.before(dateToBeChecked)) ? false
				: true;
		return statusTobeReturned;

	}

	public static Date dateCleanIngBlock(String textContaingDateinfo) {

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
		numericMonth = cal.get(Calendar.MONTH) + 1;

		numericDate = cal.get(Calendar.DATE);
		String defaultZone = "";
		String defalutyear = numericYear.toString();
		String defalutDate = numericDate.toString().length() == 1 ? ("0" + numericDate
				.toString()) : numericDate.toString();
		numericDate = null;
		String defalutmonth = numericMonth.toString();
		String defaluthourMinutes = "00:00:00";
		String htmlDocument = "";
		String amPmInfo = "";
		htmlDocument = textContaingDateinfo;
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
					} else if(defaluthourMinutes.length() == 7){
						defaluthourMinutes = defaluthourMinutes + "0";
					}
					else
					{
						String dateComponents[]=defaluthourMinutes.split(":");
						if(dateComponents[0].length()==1)
						{
							dateComponents[0]="0"+dateComponents[0];
						}
						
						if(dateComponents[1].length()==1)
						{
							dateComponents[0]="0"+dateComponents[0];
						}
						
						defaluthourMinutes=dateComponents[0]+":"+dateComponents[1]+":00";
					}
				}
			}

			String pretextContainingInformation = htmlDocument.substring(
					Math.max(begIndexForHourInfo - 30, 0), begIndexForHourInfo);
			// checks date whether Date format like yyyy-MM-DD or DD-MM-yyyy
			if (isDateContainsNumericYearDateFormat(pretextContainingInformation)) {
				Matcher matcherForNumericYearDateFormat = patternForNumericYearDateFormat
						.matcher(pretextContainingInformation);
				if (matcherForNumericYearDateFormat.find()) {
					int start = matcherForNumericYearDateFormat.start();
					int end = matcherForNumericYearDateFormat.end();
					String dateYearMonth = pretextContainingInformation
							.substring(start, end);
					String datefields[] = dateYearMonth.split("\\D");
					if (datefields[0].length() == 4) {
						String temp = datefields[0];
						datefields[0] = datefields[2];
						datefields[2] = temp;
					}
					numericYear = Integer.parseInt(datefields[2]);
					if (numericMonth == Integer.parseInt(datefields[0])) {
						defalutDate = datefields[1];
					} else if (Integer.parseInt(datefields[1]) > 12) {
						numericMonth = Integer.parseInt(datefields[0]);
						defalutDate = datefields[1];
					} else {
						defalutDate = datefields[0];
						numericMonth = Integer.parseInt(datefields[1]);
					}

				}
			} else {
				String textcontainingMonthDateInfo = null;
				Matcher matcherForYearIndate = patternforYearInDate
						.matcher(pretextContainingInformation);
				if (matcherForYearIndate.find()) {
					begIndexForYear = matcherForYearIndate.start();
					endIndexForYear = matcherForYearIndate.end();
					String yearFound = pretextContainingInformation.substring(
							begIndexForYear, endIndexForYear);
					textcontainingMonthDateInfo = pretextContainingInformation
							.replaceFirst(yearFound.substring(0,
									yearFound.length() - 1), "");
				}
				else{
					textcontainingMonthDateInfo=pretextContainingInformation;
				}
				Matcher mathcerForNumericDate = patternForDateWithNonDigitCharactes
						.matcher(textcontainingMonthDateInfo);
				if (mathcerForNumericDate.find()) {
					int begDateInfo = mathcerForNumericDate.start();
					int endDateInfotemp = mathcerForNumericDate.end();
					if (endIndexForDate == 0) {
						defalutDate = textcontainingMonthDateInfo.substring(
								begDateInfo, begDateInfo + 2);
						defalutDate = defalutDate.replaceAll("[\\D]", "");
						endIndexForDate = endDateInfotemp;
					}
					Matcher matcherForMonth = patternForMonthName
							.matcher(textcontainingMonthDateInfo);
					if (matcherForMonth.find()) {
						begIndexForMonth = matcherForMonth.start();
						int endIndexForMonthtemp = matcherForMonth.end();
						if (endIndexForMonth == 0) {
							String monthInfo = textcontainingMonthDateInfo
									.substring(begIndexForMonth,
											endIndexForMonthtemp);
							numericMonth = monthsdata.get(monthInfo.substring(
									0, 3).toLowerCase());
							endIndexForMonth = endIndexForMonthtemp;
						}
					}

					String dateObject = defalutDate + "-" + numericMonth + "-"
							+ numericYear + "~" + defaluthourMinutes + "~"
							+ amPmInfo;
					constructDateFromDateObject(dateObject, ampmInformation);
				} else {
					/*
					 * pretextContainingInformation =
					 * pretextContainingInformation .replaceFirst(defalutyear,
					 * "");
					 */
					Matcher matcherForMonthName = patternForMonthName
							.matcher(pretextContainingInformation);
					if (matcherForMonthName.find()) {
						begIndexForMonth = matcherForMonthName.start();
						endIndexForMonth = matcherForMonthName.end();
						String monthName = pretextContainingInformation
								.substring(begIndexForMonth, endIndexForMonth);
						numericMonth = monthsdata.get(monthName.toLowerCase());
						String textBetweenMonthAndHours = pretextContainingInformation
								.substring(endIndexForMonth,
										begIndexForHourInfo);
						// textBetweenMonthAndHours.
						Matcher matcherForDate = patternForDateWithNonDigitCharactes
								.matcher(textBetweenMonthAndHours);
						if (matcherForDate.find()) {
							begIndexForDate = matcherForDate.start();
							endIndexForDate = matcherForDate.end();
							defalutDate = textBetweenMonthAndHours.substring(
									begIndexForDate, endIndexForDate)
									.substring(0, 2);
						} else {
							String remainingStrigForDateLookUp = pretextContainingInformation
									.substring(0, begIndexForMonth);
							matcherForDate = patternForDateWithNonDigitCharactes
									.matcher(remainingStrigForDateLookUp);
							if (matcherForDate.find()) {
								begIndexForDate = matcherForDate.start();
								endIndexForDate = matcherForDate.end();
								defalutDate = remainingStrigForDateLookUp
										.substring(begIndexForDate,
												endIndexForDate)
										.substring(0, 2);
							}
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
							if (matcherForDate.find()) {
								begIndexForDate = matcherForDate.start();
								endIndexForDate = matcherForDate.end();
								String textContaningDate = postTextMayContainDateInfo
										.substring(begIndexForDate,
												endIndexForDate);
								defalutDate = textContaningDate.replaceAll(
										"\\D", "");
							}
						}

					}
				} else {
					Matcher matcherForDate = patternForDateWithNonDigitCharactes
							.matcher(textToBeSearched);
					if (matcherForDate.find()) {
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
		return constructDateFromDateObject(dateObject, ampmInformation);
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
				dataToBeConverted = dataToBeConverted + " " + datevalue[2].toLowerCase();
			}
			SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat
					+ hourInfo);
			try {
				dateTobeReturned = dateFormatter.parse(dataToBeConverted);
				// System.out.println(dateTobeReturned.toString());
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

	public static boolean isDateContainsNumericYearDateFormat(String textData) {
		Matcher matcherForNumericYearDate = patternForNumericYearDateFormat
				.matcher(textData);
		return matcherForNumericYearDate.find();
	}

}

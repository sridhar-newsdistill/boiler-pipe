package com.newsdistill.articleextractor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DateExtractor {
	
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
	static String dateFilterTags = ".(\\*date\\*|pub\\*|\\*info\\*|\\*time\\*|\\*calendar\\*)";
	static String regexForNumericDateFormat = "[0-9-/]{10}";
	static String regexForSelectiontags = "[class~=(?i)(.*Pub.*|.*date.*|.*info.*|.*time.*|.*calendar.*|.*post.*)]";
	static Pattern patternForZoneIdentification = Pattern
			.compile(regExForTimeZones);
	static Pattern patternForNumericDateFormat = Pattern
			.compile(regexForNumericDateFormat);
	static Pattern patternForNumericYearDateFormat = Pattern
			.compile(regexForNumericYearDateFormat);
	static Pattern patternForDateWithNonDigitCharactes = Pattern
			.compile(regexForDateWithNonDigitCharactes);
	static Pattern patternForAmPmInfo = Pattern.compile(regexForAmPm);
	static Pattern patternForHourMinuteInfo = Pattern.compile(regexForhhmmss);
	static Pattern patternForDate = Pattern.compile(regExForDate);
	static Pattern patternForMonthName = Pattern.compile(regexForMonthName);
	static Pattern patternforYearInDate = Pattern.compile(regexForYear);
	static Pattern patternval = Pattern.compile(dateFilterTags);
}

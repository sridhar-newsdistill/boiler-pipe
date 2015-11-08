package com.kohlschutter.boilerpipe.demo;

import java.util.regex.Matcher;

public class ObsolateDateIdentifier {
	/*Matcher matcherForYear = patternforYearInDate.matcher(htmlDocument);
	while (matcherForYear.find()) {
		begIndexForYear = matcherForYear.start();
		endIndexForYear = matcherForYear.end();
		defalutyear = htmlDocument.substring(begIndexForYear,
				endIndexForYear);
		if (defalutyear.length() > 4) {
			defalutyear = defalutyear.substring(0, 4);
		}
		String postTextToBeSearched = htmlDocument.substring(
				endIndexForYear,
				Math.min(endIndexForYear + 30, htmlDocument.length()));
		String pretextTobeSearched = htmlDocument.substring(
				Math.max(0, begIndexForYear - 20), begIndexForYear);
		Matcher mathcerForMonth = patternForMonthName
				.matcher(pretextTobeSearched);
		if (mathcerForMonth.find()) {
			begIndexForMonth = mathcerForMonth.start();
			endIndexForMonth = mathcerForMonth.end();
			defalutmonth = htmlDocument.substring(begIndexForMonth,
					endIndexForMonth);
			defalutmonth=monthsdata.get(defalutmonth.toLowerCase()).toString();
			String pretextForNumericDate = htmlDocument.substring(
					Math.max(0, begIndexForMonth - 10),
					begIndexForMonth);
			Matcher matcherForDate = patternForDate
					.matcher(pretextForNumericDate);
			if (matcherForDate.find()) {
				begIndexForDate = matcherForDate.start();
				endIndexForDate = matcherForDate.end();
				defalutDate = htmlDocument.substring(begIndexForDate,
						endIndexForDate);
			} else {
				String textbetweenYearMonth = htmlDocument.substring(
						endIndexForMonth, begIndexForYear);
				matcherForDate = patternForDate
						.matcher(textbetweenYearMonth);
				if (matcherForDate.find()) {
					begIndexForDate = matcherForDate.start();
					endIndexForDate = matcherForDate.end();
					defalutDate = textbetweenYearMonth.substring(
							begIndexForDate, endIndexForDate);
				} else {
					String numericDateAfterYear = htmlDocument
							.substring(endIndexForYear, Math.min(
									endIndexForDate + 5,
									htmlDocument.length()));
					matcherForDate = patternForDate
							.matcher(numericDateAfterYear);
					if (matcherForDate.find()) {
						begIndexForDate = matcherForDate.start();
						endIndexForDate = matcherForDate.end();
						defalutDate = numericDateAfterYear.substring(
								begIndexForDate, 2);
					}
				}
			}

		} else if (endIndexForMonth == 0 || endIndexForDate == 0) {

			mathcerForMonth = patternForMonthName
					.matcher(postTextToBeSearched);
			if (mathcerForMonth.find()) {
				begIndexForMonth = mathcerForMonth.start();
				begIndexForMonth = endIndexForYear + begIndexForMonth;
				endIndexForMonth = mathcerForMonth.end();
				endIndexForMonth = endIndexForYear + endIndexForMonth;
				defalutmonth = htmlDocument.substring(begIndexForMonth,
						endIndexForMonth);
				String textBetweenMonthAndYear = htmlDocument
						.substring(endIndexForYear, begIndexForMonth);
				Matcher matcherForDate = patternForDate
						.matcher(textBetweenMonthAndYear);
				if (matcherForDate.find()) {
					begIndexForDate = matcherForDate.start();
					endIndexForDate = matcherForDate.end();
					defalutDate = textBetweenMonthAndYear.substring(
							begIndexForDate, endIndexForDate);
				} else {
					String postTextForDate = htmlDocument.substring(
							endIndexForMonth,
							Math.min(endIndexForMonth + 5,
									htmlDocument.length()));
					matcherForDate = patternForDate
							.matcher(postTextForDate);
					if (matcherForDate.find()) {
						begIndexForDate = matcherForDate.start();
						endIndexForDate = matcherForDate.end();
						defalutDate = postTextForDate.substring(
								begIndexForDate, endIndexForDate);
					} else {
						// incase month lies left hand and date lies
						// right hand
						String pretextBeforeyear = htmlDocument
								.substring(Math.max(0,
										begIndexForYear - 10),
										begIndexForYear);
						matcherForDate = patternForDate
								.matcher(pretextBeforeyear);
						begIndexForDate = matcherForDate.start();
						endIndexForDate = matcherForDate.end();
						defalutDate = pretextBeforeyear.substring(
								begIndexForDate, endIndexForDate);

					}
				}
			}

		}

		else {
			String pretextBeforeyear = htmlDocument.substring(
					Math.max(0, begIndexForYear - 10), begIndexForYear);
			Matcher matcherForDate = patternForDate
					.matcher(pretextBeforeyear);
			begIndexForDate = matcherForDate.start();
			endIndexForDate = matcherForDate.end();
			defalutDate = pretextBeforeyear.substring(begIndexForDate,
					endIndexForDate);

		}

	}*/
}

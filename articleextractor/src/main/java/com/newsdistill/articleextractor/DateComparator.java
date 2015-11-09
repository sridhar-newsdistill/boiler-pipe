package com.newsdistill.articleextractor;

import java.util.Comparator;

public class DateComparator implements Comparator<String> {

	@Override
	public int compare(String date1, String date2) {
		// TODO Auto-generated method stub

		String date1arr[] = date1.split("@");

		String date2arr[] = date2.split("@");

		return (Integer.parseInt(date1arr[1]) > Integer.parseInt(date2arr[1])) ? -1

				: Integer.parseInt(date1arr[1]) == Integer

				.parseInt(date2arr[1]) ? 0 : 1;

	}

}

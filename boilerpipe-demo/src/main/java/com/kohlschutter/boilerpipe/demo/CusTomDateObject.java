package com.kohlschutter.boilerpipe.demo;

import java.util.Date;

public class CusTomDateObject implements Comparable<CusTomDateObject> {
	Date articleDate;

	public void setDate(Date date) {
		this.articleDate = date;
	}
	public Date getDate()
	{
		return articleDate;
	}

	@Override
	public int compareTo(CusTomDateObject arg0) {

		return 0;
	}

}

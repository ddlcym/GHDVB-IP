package com.changhong.app.utils;

import java.util.Calendar;
import java.util.TimeZone;

import com.changhong.app.dtv.P;

public class UserDate {

	public String mYear;
	public String mMonth;
	public String mDay;
	public String mWeek;
	public String mHour, mMinute, mSeconds;
	private static String[] week = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

	public UserDate() {

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		
		mYear = String.valueOf(c.get(Calendar.YEAR));

		if ((c.get(Calendar.MONTH) + 1) < 10) {
			mMonth = "0" + String.valueOf(c.get(Calendar.MONTH) + 1);
		} else {
			mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		}
		
		if (c.get(Calendar.DAY_OF_MONTH) < 10) {
			mDay = "0" + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		} else {
			mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		}	
		
		mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

	
		if (c.get(Calendar.HOUR_OF_DAY) < 10) {
			mHour = "0" + String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		} else {
			mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		}
		if (c.get(Calendar.MINUTE) < 10) {
			mMinute = "0" + String.valueOf(c.get(Calendar.MINUTE));
		} else {
			mMinute = String.valueOf(c.get(Calendar.MINUTE));
		}
		if (c.get(Calendar.MINUTE) < 10) {
			mSeconds = "0" + String.valueOf(c.get(Calendar.SECOND));
		} else {
			mSeconds = String.valueOf(c.get(Calendar.SECOND));
		}

		if ("1".equals(mWeek)) {
			mWeek = week[6];
		} else if ("2".equals(mWeek)) {
			mWeek = week[0];
		} else if ("3".equals(mWeek)) {
			mWeek = week[1];
		} else if ("4".equals(mWeek)) {
			mWeek = week[2];
		} else if ("5".equals(mWeek)) {
			mWeek = week[3];
		} else if ("6".equals(mWeek)) {
			mWeek = week[4];
		} else if ("7".equals(mWeek)) {
			mWeek = week[5];
		}
	}

	public String getYear() {
		return this.mYear;
	}

	public String getMonth() {
		return this.mMonth;
	}

	public String getDay() {
		return this.mDay;
	}

	public String getHours() {
		return this.mHour;
	}

	public String getMinutes() {
		return this.mMinute;
	}

	public String getSeconds() {
		return this.mSeconds;
	}
	public String getMDHM() {
		return this.mMonth+this.mDay+this.mHour+this.mMinute;
	}	
}

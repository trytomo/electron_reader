package com.example.uhf_bt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
	public final static String DATEFORMAT_FULL="yyyy-MM-dd HH:mm:ss";
	public final static String DATEFORMAT_YMD="yyyy-MM-dd";
	public final static String DATEFORMAT_YMDH="yyyy-MM-dd HH";
	public final static String DATEFORMAT_YMDHM="yyyy-MM-dd HH:mm";
	public final static String DATEFORMAT_Y="yyyy";
	public final static String DATEFORMAT_MM="MM";
	public final static String DATEFORMAT_DD="dd";
	public final static String DATEFORMAT_HMS="HH:mm:ss";
	public final static String DATEFORMAT_HM="HH:mm";
	public final static String DATEFORMAT_HH="HH";
	public final static String DATEFORMAT_mm="mm";
	public final static String DATEFORMAT_ss="ss";

	public static int CALENDAR_FIRST_DAY_OF_WEEK = Calendar.SUNDAY;
	public static int MAX_WEEK_DAYS = 7;

	public static int getMonthDays(int year, int month) {
		month++;
		switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				return 31;
			case 4:
			case 6:
			case 9:
			case 11:
				return 30;
			case 2:
				if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
					return 29;
				} else {
					return 28;
				}
			default:
				return  -1;
		}
	}

	public static int getFirstDayWeek(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static String[] getDayOfWeek(String pattern) {
		return getDayOfWeekOrMonthOrYear(pattern, Calendar.DAY_OF_WEEK);
	}

	public static String[] getDayOfMonth(String pattern) {
		return getDayOfWeekOrMonthOrYear(pattern, Calendar.DAY_OF_MONTH);
	}

	public static String[] getDayOfYEAR(String pattern) {
		return getDayOfWeekOrMonthOrYear(pattern, Calendar.DAY_OF_YEAR);
	}

	private static String[] getDayOfWeekOrMonthOrYear(String pattern, int field) {
		String[] days = new String[2];
		SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);

		Calendar cal = getCalendar(CALENDAR_FIRST_DAY_OF_WEEK);
		cal.set(field, 1);
		days[0] = dateFormat.format(cal.getTime());

		cal.set(field, cal.getActualMaximum(field));
		days[1] = dateFormat.format(cal.getTime());

		return days;
	}

	public static String[] getDaysOfWeekForDate(String pattern, String time) {
		SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
		try {
			Date date = dateFormat.parse(time);
			Calendar cal = getCalendar(CALENDAR_FIRST_DAY_OF_WEEK, date);
			int weekDays = cal.getActualMaximum(Calendar.DAY_OF_WEEK);
			String[] days = new String[weekDays];
			for (int i = 0; i < weekDays; i++) {
				cal.set(Calendar.DAY_OF_WEEK, i + 1);
				days[i] = dateFormat.format(cal.getTime());
			}
			return days;
		} catch (ParseException e) {
			return new String[] {};
		}
	}

	public static List<String> getDayListOfWeekForDate(String pattern, String time) {
		String[] days = getDaysOfWeekForDate(pattern, time);
		List<String> dayList = new ArrayList<>();
		for(int i=0; i<days.length; i++) {
			dayList.add(days[i]);
		}
		return dayList;
	}

	public static String getFirstDayOfWeek(int year, int weekIndex, String pattern) {
		SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(CALENDAR_FIRST_DAY_OF_WEEK);
		cal.setMinimalDaysInFirstWeek(MAX_WEEK_DAYS);

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		cal.add(Calendar.DATE, weekIndex * 7);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

		return dateFormat.format(cal.getTime());
	}

	private static SimpleDateFormat getSimpleDateFormat(String pattern) {
		SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	private static Calendar getCalendar(int firstDayOfWeek) {
		return getCalendar(firstDayOfWeek, new Date());
	}

	private static Calendar getCalendar(int firstDayOfWeek, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(firstDayOfWeek);
		cal.setTime(date);
		return cal;
	}

	public static int getWeekOfYear(String pattern, String time) throws ParseException {
		SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
		Date date = dateFormat.parse(time);

		Calendar cal = getCalendar(CALENDAR_FIRST_DAY_OF_WEEK, date);
		cal.setMinimalDaysInFirstWeek(MAX_WEEK_DAYS);

		int weeks = cal.get(Calendar.WEEK_OF_YEAR);
		int month = cal.get(Calendar.MONTH);

		if(month >= 11 && weeks == 1) {
			weeks += 52;
		}
		return weeks;
	}

	public static String getFormatDate(String pattern, long time) {
		SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
		Date date = new Date(time);
		return dateFormat.format(date);
	}

	public static String getFormatDateByDays(String pattern, int days) {
		Date date = new Date();
		Calendar cal = getCalendar(CALENDAR_FIRST_DAY_OF_WEEK, date);

		cal.add(Calendar.DATE, days);
		SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
		return dateFormat.format(cal.getTime());
	}

	public static String getCurrFormatDate(String pattern) {
		return getFormatDate(pattern, System.currentTimeMillis());
	}

	public static int betwweenDay(String beforeDay, String afterDay, String pattern) {
		SimpleDateFormat simpleDateFormat = getSimpleDateFormat(pattern);
		int days = 0;
		try {
			Date beforeDate = simpleDateFormat.parse(beforeDay);
			Date afterDate = simpleDateFormat.parse(afterDay);
			days = betweenDays(beforeDate, afterDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return days;
	}

	public static int betweenDays(Date beforeDate, Date afterDate) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(beforeDate);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(afterDate);
		int day1= cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if(year1 != year2) {
			int timeDistance = 0;
			for(int i = year1 ; i < year2 ; i ++) {
				if(i%4==0 && i%100!=0 || i%400==0) {
					timeDistance += 366;
				} else {
					timeDistance += 365;
				}
			}
			return timeDistance + (day2-day1);
		} else {
			return day2 - day1;
		}
	}

	public static String getCurrYearString() {
		return getFormatDate(DATEFORMAT_Y, System.currentTimeMillis());
	}

	public static int getCurrYearInteger() {
		String year = getCurrYearString();
		return Integer.valueOf(year);
	}

	public static String getCurrMonthString() {
		return getFormatDate(DATEFORMAT_MM, System.currentTimeMillis());
	}

	public static int getCurrMonthInteger() {
		String month = getCurrMonthString();
		return Integer.valueOf(month);
	}

	public static String getCurrDayString() {
		return getFormatDate("dd", System.currentTimeMillis());
	}

	public static int getCurrDayInteger() {
		String month = getCurrDayString();
		return Integer.valueOf(month);
	}

	public static String getCurrHourString() {
		return getFormatDate(DATEFORMAT_HH, System.currentTimeMillis());
	}

	public static int getCurrHourInteger() {
		String month = getCurrHourString();
		return Integer.valueOf(month);
	}

	public static String getCurrMinuteString() {
		return getFormatDate(DATEFORMAT_mm, System.currentTimeMillis());
	}

	public static int getCurrMinuteInteger() {
		String month = getCurrMinuteString();
		return Integer.valueOf(month);
	}

	public static String getCurrSecondString() {
		return getFormatDate(DATEFORMAT_ss, System.currentTimeMillis());
	}

	public static int getCurrSecondInteger() {
		String month = getCurrMinuteString();
		return Integer.valueOf(month);
	}

	public static void setCalendarFirstDayOfWeek(int calendarFirstDayOfWeek) {
		CALENDAR_FIRST_DAY_OF_WEEK = calendarFirstDayOfWeek;
	}
}

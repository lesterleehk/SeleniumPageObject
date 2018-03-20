package com.java.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.config.Config;

public class DateTimeUtils {
	public static String getCurrentTimeAsStr(){
		Calendar cal = Calendar.getInstance();
		return new SimpleDateFormat(Config.getInstance().getProperty("timeFormat")).format(cal.getTime());
	}
	
	public static Calendar strToCalendarDate(String dateStr){
		Calendar cal = Calendar.getInstance();
		try {
			String dateFormat = Config.getInstance().getProperty("dateFormat");
			Date date = new SimpleDateFormat(dateFormat).parse(dateStr);
			cal.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cal;
	}

	public static int getMonthAsInt(String dateString){
		Calendar cal = DateTimeUtils.strToCalendarDate(dateString);

		return cal.get(Calendar.MONTH) + 1; // the months are numbered
													// from 0 (January) to
													// 11 (December).
	}
	public static int getYearAsInt(String dateString){
		Calendar cal = DateTimeUtils.strToCalendarDate(dateString);

		return cal.get(Calendar.YEAR);
	}
	public static int getDayAsInt(String dateString){
		Calendar cal = DateTimeUtils.strToCalendarDate(dateString);
		return  cal.get(Calendar.DAY_OF_MONTH);
	}
	public static String calendarDateToString(Calendar date){
		String dateStr = new SimpleDateFormat(Config.getInstance().getProperty("dateFormat")).format(date);
		return dateStr;
	}

	public static String calendarDateToString(Calendar date, int dateDifferent){
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, dateDifferent);
		String dateStr = new SimpleDateFormat(Config.getInstance().getProperty("dateFormat")).format(now.getTime());
		return dateStr;
	}
	
	public static String calendarBeginDateOfYear(Calendar date, int yearDifferent){
		Calendar now = Calendar.getInstance();
		int  currentYear =now.get(Calendar.YEAR);
	
		DateFormat dateFormat = new SimpleDateFormat(Config.getInstance().getProperty("dateFormat"));
		String inputString = "Jan 1, "+(currentYear+yearDifferent);
		Date beginDateOfTheYear=null;
		try {
			beginDateOfTheYear = dateFormat.parse(inputString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SimpleDateFormat(Config.getInstance().getProperty("dateFormat")).format(beginDateOfTheYear.getTime());
	}
	
	public static String calendarEndDateOfYear(Calendar date, int yearDifferent){
		Calendar now = Calendar.getInstance();
		int  currentYear =now.get(Calendar.YEAR);
	
		DateFormat dateFormat = new SimpleDateFormat(Config.getInstance().getProperty("dateFormat"));
		String inputString = "Dec 31, "+(currentYear+yearDifferent);
		Date beginDateOfTheYear=null;
		try {
			beginDateOfTheYear = dateFormat.parse(inputString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SimpleDateFormat(Config.getInstance().getProperty("dateFormat")).format(beginDateOfTheYear.getTime());
	}

	public static String dateToString(Date date){
		String dateStr = new SimpleDateFormat(Config.getInstance().getProperty("dateFormat")).format(date);
		return dateStr;
	}
	
	public static String getTodayAsString(){
		Calendar now = Calendar.getInstance();
		return dateToString(now.getTime());
	}

	public static String getTimeStamp(){
		Date now = new Date();
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(now);
		return timeStamp;
	}
}

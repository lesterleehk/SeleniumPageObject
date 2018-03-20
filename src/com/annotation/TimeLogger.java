package com.annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.abstractclasses.TestObject;
import com.config.Config;
import com.junit.TestReport;

import org.openqa.selenium.WebDriver;

public class TimeLogger extends NetDTestWatcher {
	private WebDriver driver;
	
	public TimeLogger(WebDriver driver) {	
        this.driver =  driver;  
    }
	public boolean isSkipClass(TestObject obj) {
		return false;// currently we don't have any class use this so always return false
	}

	public boolean isSkipMethod(Method method) {
		// Process @Schedule
		// if method is annotated with @Schedule
		boolean isSkipNeeded=false;
		if (method.isAnnotationPresent(Schedule.class)) {
			Annotation annotation = method.getAnnotation(Schedule.class);
			Schedule period = (Schedule) annotation;
			// check if method is skippable
			if (period.Monthly()) {
				isSkipNeeded=true; // turn on skip first, and if skip condition match, then skip it
				DateFormat dateFormat = new SimpleDateFormat("dd");
				Calendar cal = Calendar.getInstance();
				int day = Integer.valueOf(dateFormat.format(cal.getTime()));
				// when day ==1, run the testing
				if (day == 10 || day==20)
					isSkipNeeded= false;
			}
			if (period.Weekly()) {
				isSkipNeeded=true;// turn on skip first, and if skip condition match, then skip it
				Calendar cal = Calendar.getInstance();
				int day = cal.get(Calendar.DAY_OF_WEEK);
				// when day ==Sunday(1), run the testing
				if (day == 1)
					isSkipNeeded= false;
			}
			if (isSkipNeeded)
				System.out.println(method.getName()+ " is skipped on purpose because today is not its scheduled day either monthy = day 1, 20 or weekly = sunday");
		}
		return isSkipNeeded;
	}

	public String TCStartTime;
	public String UIstatus;

	@Override
	public void start(TestObject obj) {

		// Test Case timestamp
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
		Date date = new Date();
		TCStartTime = dateFormat.format(date);
	}
	
	@Override
	public void finished(TestObject obj) {

		String logdetails = this.retreiveEKPlog();	
		if (!(logdetails.trim() == "")) {
			TestReport.EXCEL_LOCK.lock();
			TestReport logger_testrpt = new TestReport();
			logger_testrpt.SaveEKPErrToExcel(
				logdetails+ System.lineSeparator(), this);
			
			TestReport.EXCEL_LOCK.unlock();
		}
	}
	
	@Override
	public void failed(Throwable e, TestObject obj) {
		// TODO Auto-generated method stub
		UIstatus="FAIL";
	}

	@Override
	public void succeeded(TestObject obj) {
		// TODO Auto-generated method stub
		UIstatus="PASS";
	}
	
	private String retreiveEKPlog() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
		String TodayDate = TCStartTime.substring(0, 11);
		String sEKPExpt = "";
		File EKPLogFile = new File(Config.getInstance().getProperty("ekp.log", driver));
		Scanner scnr;

		try {
			scnr = new Scanner(EKPLogFile).useDelimiter("\t");
			String line = null;

			Date startTime;
			try {
				startTime = sdf.parse(TCStartTime);
				while (scnr.hasNextLine()) {
					line = scnr.nextLine();
					if (line.contains(TodayDate)) {
						Date logTime = sdf.parse(line.substring(0, 20));
						if (logTime.after(startTime)) {
							if (line.contains("SYSTEM EXCEPTION")) {
								sEKPExpt = line;
								while (scnr.hasNextLine()) {
									line = scnr.nextLine();
									sEKPExpt += line;
								}
							}
						}
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Date endTime = sdf.parse(TCEnd);

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return sEKPExpt;
	}
	

	
	
}

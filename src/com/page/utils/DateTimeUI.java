package com.page.utils;

import org.openqa.selenium.WebDriver;

import com.java.utils.Validate;

public class DateTimeUI {
	
	public static void setDates_UI(WebDriver driver, String sDate, String xpath_calendar){
		if(!Validate.isBlank(sDate)){	
			InteractionUtils.dateSelect_Calandar(driver, sDate, xpath_calendar);			
		}		
	} 

}

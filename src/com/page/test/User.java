package com.page.test;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.pageobject.CourseLaunchPageWithElements;
import com.pageobject.HomePageWithElements;
import com.pageobject.TakeExamPageWithElements;

/**
 * 
 * @author lester.li
 * 
 */
public class User extends TestObject {


	public HomePageWithElements homePage;
	public CourseLaunchPageWithElements courseLaunchPage;
	public TakeExamPageWithElements takeExamPage;

	public User() {
		super();
	}

	public User(String UID, String PWD) {
		
		this.setUID(UID);
		this.setPWD(PWD);
	}
	
	public void initilizeElements(WebDriver driver) {
		homePage = new HomePageWithElements(driver, this);
	}

	public void LoginTest(WebDriver driver) {
	
		homePage.Login();
		homePage.Logout();

	    
	}



	

	
}
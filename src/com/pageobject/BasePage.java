package com.pageobject;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.config.Config;
import com.page.navigate.HTMLElementManager;
import com.page.navigate.PageInteraction;

public abstract class BasePage {
	
	PageInteraction page;
	HTMLElementManager htmlElements;
	TestObject testCaseData;
	WebDriver driver;
	
	private static String commonHTMLELEMENTFile=Config.getInstance().getProperty("commonHTMLElements");
	
	
	public BasePage(WebDriver driver, TestObject testCaseData) {
		htmlElements = new HTMLElementManager(commonHTMLELEMENTFile);
		driver=driver;
		page = new PageInteraction(htmlElements, driver);
		testCaseData=testCaseData;
	}
	
	public abstract void goToPage();
	
	public void Login() {
		page.findElementToSetValue("elementName", testCaseData.getUID());
		page.findElementToSetValue("elementName", testCaseData.getPWD());
		page.findElementToClick("elementid");
		page.checkIfElementPresent("elementid");
	}
	public void Logout() {
		page.findElementToClick("elementid");
		page.checkIfElementPresent("elementid");
	}
	public void CheckSaveSucess() {
		//Centralize different page save checking
		
	}
	public void checkExpectedResult() {
		//Centralize different page result checking
	}
	public void changeSysConfig() {
		
	}
	public void switchToPopUpWin() {
		page.utils.switchToPopUpWin(driver);
	}
	public void closeAllPopUpWins() {
		page.utils.closeAllPopUpWins(driver);
	}
	public void switchToParentWin() {
		page.utils.switchToParentWin(driver);
	}
	public void switchToBaseWin() {
		page.utils.switchToBaseWin(driver);
	}
	

}

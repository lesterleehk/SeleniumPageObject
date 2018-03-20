package com.pageobject;

import java.io.File;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.page.navigate.PageInteraction;
import com.page.navigate.HTMLElementManager;

public class HomePageWithElements  extends BasePage{

	String PageURL= "PageURL";

	
	public HomePageWithElements(WebDriver driver, TestObject obj) {
		super(driver,obj);
		File file = new File(HomePageWithElements.class.getClassLoader().getResource("/elements/PageObjectWithElements.csv").getFile());
		htmlElements.AddPageElements(file);

		
	}


	
	public void SearchToEnrollCourse() {
		page.findElementToClick("elementid");
		page.findElementToSetValue("elementName", testCaseData.getValue("thevalue"));
		page.checkIfElementPresent("elementid");
		page.utils.switchToBaseWin(driver);
		page.findElementToClick("enrollid");
		page.findElementToClick("confirmenrollid");
		page.utils.closeAllPopUpWins(driver);
		
	}
	public void goToPage() {
		driver.navigate().to(PageURL);
	}

}

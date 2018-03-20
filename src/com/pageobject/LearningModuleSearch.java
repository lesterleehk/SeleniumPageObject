package com.pageobject;

import java.io.File;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.page.navigate.HTMLElementManager;
import com.page.navigate.PageInteraction;

public class LearningModuleSearch  extends BasePage {
	

	String PageURL= "LearningModuleSearchURL";

	
	public LearningModuleSearch(WebDriver driver, TestObject obj) {
		super(driver,obj);
		File file = new File(HomePageWithElements.class.getClassLoader().getResource("/elements/LearningModuleSearch.csv").getFile());
		htmlElements.AddPageElements(file);

		
	}	

	public void SearchModuleToOpen() {
		page.findElementToClick("elementid");
		page.utils.switchToPopUpWin(driver);
	}
	public void goToPage() {
		driver.navigate().to(PageURL);
	}
}

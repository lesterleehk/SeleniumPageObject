package com.page.test;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.pageobject.CatalogEditorModuleProperties;
import com.pageobject.CatalogEditorSessionProperties;
import com.pageobject.CourseLaunchPageWithElements;
import com.pageobject.HomePageWithElements;
import com.pageobject.LearningModuleSearch;
import com.pageobject.TakeExamPageWithElements;

public class LearningModule  extends TestObject  {
	
	public HomePageWithElements homePage;
	public CatalogEditorModuleProperties catalogmodulePage;
	public CatalogEditorSessionProperties catalogSessionPage;
	
	public LearningModuleSearch moduleSearchPage;
	
	public void initilizeElements(WebDriver driver) {
		homePage = new HomePageWithElements(driver, this);
		catalogmodulePage = new CatalogEditorModuleProperties(driver, this);
		catalogSessionPage= new CatalogEditorSessionProperties(driver, this);
		moduleSearchPage= new LearningModuleSearch(driver, this);
		
	}

	public void editProperties(WebDriver driver) {
	
		homePage.Login();
		moduleSearchPage.SearchModuleToOpen();
		catalogmodulePage.FillUpModuleProperties();
		catalogmodulePage.GoToSessionProperties();
		catalogmodulePage.switchToPopUpWin();
		catalogSessionPage.FillUpSessionProperties();
		
	    
	}

}

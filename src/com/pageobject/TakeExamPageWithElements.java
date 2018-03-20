package com.pageobject;

import java.io.File;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.page.navigate.PageInteraction;
import com.page.navigate.HTMLElementManager;

public class TakeExamPageWithElements  extends BasePage {

	String PageURL= "PageURL";

	
	public TakeExamPageWithElements(WebDriver driver, TestObject obj) {
		super(driver,obj);
		File file = new File(HomePageWithElements.class.getClassLoader().getResource("/elements/PageObjectWithElements.csv").getFile());
		htmlElements.AddPageElements(file);

	}	
	
	public void AnswerMCQuestion(TestObject testCaseData) {
		page.findElementToClick("elementid");
		page.findElementToSetValue("elementName", testCaseData.getValue("thevalue"));
		page.checkIfElementPresent("elementid");
		
	}
	public void AnswerTFQuestion(TestObject testCaseData) {
		page.findElementToClick("elementid");
		page.findElementToSetValue("elementName", testCaseData.getValue("thevalue"));
		page.checkIfElementPresent("elementid");
		
	}
	public void SubmitExam() {
		page.findElementToClick("elementid");
	}
	public void goToPage() {
		driver.navigate().to(PageURL);
	}
}

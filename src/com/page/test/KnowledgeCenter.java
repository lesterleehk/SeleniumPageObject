package com.page.test;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.pageobject.CourseLaunchPageWithElements;
import com.pageobject.HomePageWithElements;
import com.pageobject.TakeExamPageWithElements;

public class KnowledgeCenter extends TestObject  {

	public HomePageWithElements homePage;
	public CourseLaunchPageWithElements courseLaunchPage;
	public TakeExamPageWithElements takeExamPage;
	
	public void initilizeElements(WebDriver driver) {
		homePage = new HomePageWithElements(driver, this);
		courseLaunchPage = new CourseLaunchPageWithElements(driver, this);
		takeExamPage= new TakeExamPageWithElements(driver, this);
		
	}

	public void runCompleteCourse(WebDriver driver) {
	
		homePage.Login();
		homePage.SearchToEnrollCourse();
		courseLaunchPage.LaunchCourse();
		courseLaunchPage.CompleteCourse();
	    
	}

	
	public void runCompleteExam(WebDriver driver, ArrayList<TestObject> question) {
		
		homePage.Login();
		homePage.SearchToEnrollCourse();
		courseLaunchPage.LaunchCourse();
		
	    for (TestObject q: question) {
	    	if (q.getValue("QuesType").equalsIgnoreCase("MC")) {
	    		takeExamPage.AnswerMCQuestion(q);
	    	}else if (q.getValue("QuesType").equalsIgnoreCase("TF")) {
	    		takeExamPage.AnswerTFQuestion(q);
	    	}
	    	
	    }
	    takeExamPage.SubmitExam();
	}

}

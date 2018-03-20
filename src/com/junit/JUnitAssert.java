package com.junit;


import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.page.navigate.Navigator;
import com.page.utils.InteractionUtils;




/**This class overwrites JUnit.Assertion to provide more debug info.
 * 
 * @author lester.li
 *
 */
public class JUnitAssert {
	
	public static void assertEquals(String expectedResult, String actualResult){
		boolean matched = expectedResult.equalsIgnoreCase(actualResult);
		if(!matched){
			System.out.println("Expected:" + expectedResult + "; actual:" + actualResult);
			//TestReport.getInstance().SaveFailReportToExcel("Expected:" + expectedResult + "; actual:" + actualResult+System.lineSeparator());
			org.junit.Assert.fail("Expected:" + expectedResult + "; actual:" + actualResult);
		}
	}
	
	public static void assertTrue(boolean condition, String msgForFail){
		if(!condition){
			System.out.println(msgForFail);
			//TestReport.getInstance().SaveFailReportToExcel(msgForFail+System.lineSeparator());
			org.junit.Assert.fail(msgForFail);	
		}
	}
	
	
	public static void assertElementIsPresent(WebDriver driver, String msgForFail,By by){//this is element must present to pass
	
		boolean condition = InteractionUtils.isElementPresent(driver, by);
		if(!condition){
			System.out.println(msgForFail);
			//TestReport.getInstance().SaveFailReportToExcel(msgForFail+System.lineSeparator());
			org.junit.Assert.fail(msgForFail);	
		}
	}
	/*
	 * Normal successful message
	 */
	public static void assertSuccessMessagebar(WebDriver driver,String msgForFail){
		By msgBarBy = By.xpath("//ul[@class='success messagebar']");
		// wait for the msg bar to show up
		int countTimes=0;
		boolean SuccessMessagebarPresented =false;
		while( countTimes<100){
			if (InteractionUtils.isElementPresent(driver, msgBarBy)){
				SuccessMessagebarPresented=true;
				break;
			}
			System.out.println("success messagebar is not shown up yet");
			Navigator.explicitWait(100);
			countTimes++;
		}
		
		if(!SuccessMessagebarPresented){
			System.out.println(msgForFail);
			org.junit.Assert.fail(msgForFail);	
		}else{
			Navigator.waitForPageLoad(driver);
			while(InteractionUtils.isElementPresent(driver, msgBarBy)){
				Navigator.explicitWait(1000);
				//System.out.println("success messagebar is not disappear yet");
			};
		}
	}
	
	/*
	 * General WarningMessagebar
	 */
	public static void assertWarningMessagebar(WebDriver driver,String msgForFail){
		By failMsgBarBy = By.xpath("//ul[@class='warning messagebar']");//drop-down bar
		boolean condition = InteractionUtils.elementShouldPresent(driver, failMsgBarBy); 
		
		if(!condition){
			System.out.println(msgForFail);
			org.junit.Assert.fail(msgForFail);	
		}
	}
	/*
	 * Simple Highlighted under textbox 
	 * eg: Required*
	 */
	public static void assertError(WebDriver driver,String msgForFail){
		By errorMsgBy = By.xpath("//*[@class='error']");//Required*	
		boolean condition = InteractionUtils.elementShouldPresent(driver, errorMsgBy);
		
		if(!condition){
			System.out.println(msgForFail);
			org.junit.Assert.fail(msgForFail);	
		}
	}
	/*
	 * Fail plain text msg in new page: 
	 * eg: Field Cannot Be Empty
	 */
	public static void assertMessageViewMessagekey(WebDriver driver,String msgForFail){
		By errorMsgNewPageBy = By.xpath("//*[@class='message-view-messagekey']");//Module Attribute Categories--warning message will show in new page
		
		boolean condition =InteractionUtils.elementShouldPresent(driver, errorMsgNewPageBy);
		if(!condition){
			System.out.println(msgForFail);
			org.junit.Assert.fail(msgForFail);	
		}
	}
	/*
	 * Plain text without color in orginal page
	 * Eg: Question Editor> duplicated ID
	 */
	public static void assertEkpWarning(WebDriver driver,String msgForFail){
		By ekpwarningBy = By.xpath("//*[@class='ekpwarning']");
		boolean condition = InteractionUtils.elementShouldPresent(driver, ekpwarningBy);
					
		if(!condition){
			System.out.println(msgForFail);
			org.junit.Assert.fail(msgForFail);	
		}
	}
	/*
	 * Highlighed Fail msg in orginal page: 
	 * eg: Competency Manager
	 */
	public static void assertWarning(WebDriver driver,String msgForFail){
		By warningBy = By.xpath("//*[@class='warning']");
		
		boolean condition =	InteractionUtils.elementShouldPresent(driver, warningBy);
		
		if(!condition){
			System.out.println(msgForFail);
			org.junit.Assert.fail(msgForFail);	
		}
	}
}

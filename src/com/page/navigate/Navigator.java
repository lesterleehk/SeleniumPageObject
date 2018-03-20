package com.page.navigate;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.abstractclasses.TestObject;
import com.config.Config;
import com.controller.TestDriverUtils;
import com.controller.TestDriverUtils.Type;
import com.java.utils.Validate;
import com.page.utils.InteractionUtils;



public class Navigator {
	
// Suppress default constructor for noninstantiability
	
	private Navigator() {

		throw new AssertionError();
	}
	
	public enum TargetPages {
	    KC,
	    ;
	    
	};
	
	
	public static final XMLWebElementManager xmlWebElmtMgr = XMLWebElementManager.getInstance();
	
	public static void navigate(WebDriver driver,ArrayList<WebElementWrapper> webElementList, TestObject obj){
		
		InteractionUtils.acceptAlertIfPresent(driver);
		InteractionUtils.closeAllPopUpWins(driver);
		WebElementWrapper parentWE = webElementList.get(0);
	
		// if parent link presented that means need to click the link to navigate 
		if(InteractionUtils.getHowManyByPresntInPage(driver, parentWE.getBy(), false) > 0){
			InteractionUtils.clickLink(driver, parentWE.getBy());
			Navigator.waitForPageLoad(driver);	
		}
		//skip first webelemnt as it is parent
		for (int i = 1; i < webElementList.size(); i++) {
			WebElementWrapper we = webElementList.get(i);
			By by = we.getBy();
			Navigator.waitForPageLoad(driver);
			InteractionUtils.acceptAlertIfPresent(driver);
			if (we.getSeleniumByType().equalsIgnoreCase((WebElementWrapper.URL))){
				driver.navigate().to(we.getSeleniumByValue());
				continue;
			}
			
			if(InteractionUtils.getHowManyByPresntInPage(driver, by, false) == 0){
				Navigator.explicitWait(3000);
				InteractionUtils.acceptAlertIfPresent(driver);
			}
			
			if (we.isTopMenu()){
				waitElementToDisplay(driver, by);
				InteractionUtils.mouseOver(driver, by);
				Navigator.explicitWait(500);
				
				/*Robot r;
				try {
					r = new Robot();
					r.mouseMove(1,1);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			else{
				waitElementToDisplay(driver, by, 2000);
				InteractionUtils.clickLink(driver, by);	
			}		
		}
	}

	public static void explicitWait(){
		explicitWait(Integer.parseInt(Config.getInstance().getProperty("ExplicitWait_millis")));
	}

	
	public static void waitElementToDisplay(WebDriver driver, By by, int minisecond){
		boolean timeout=false;
		WebElement elementToClick = driver.findElement(by);
		waitForJStoLoad(driver);
		while((!elementToClick.isDisplayed())){
     		if (timeout){
     			System.err.println("***************Warning*********"+elementToClick.toString()+" Element is InVisible*************");
     			break;
     		}else{
     			Navigator.explicitWait(minisecond);
     			timeout=true;
     		}
     	}
	}
	
	public static void waitElementToDisplay(WebDriver driver, By by){
		waitElementToDisplay(driver, by, 5000);
	}
	
	public static void explicitWait(long wait_millis){
		try {
			Thread.sleep(wait_millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void refresh(WebDriver driver){
		driver.navigate().to(driver.getCurrentUrl());
	}
	/**Wait for Elements to occur
	 * 
	 * @param driver
	 * @param by
	 */
	public static void waitForElementLoad(WebDriver driver, final By by){
		/*Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(Integer.parseInt(Config.getInstance().getProperty("WaitAjaxElment_millis")), TimeUnit.MILLISECONDS)
				.pollingEvery(300, TimeUnit.MILLISECONDS)
				.ignoring(Exception.class);
		wait.until(ExpectedConditions.presenceOfElementLocated(by));*/
		waitForPageLoad(driver);
		double  startTime;
		double  endTime,totalTime;
		Double period = Double.parseDouble(Config.getInstance().getProperty("WaitAjaxElment_millis", driver));
		int size = InteractionUtils.getHowManyByPresntInPage(driver,by, false);
		startTime = System.currentTimeMillis();
		 while(size<=0){
		  	
		  	endTime = System.currentTimeMillis();
		  	totalTime = endTime - startTime;
		  	
		  	if (totalTime>period){
		  		//explicitWait();
		         throw new RuntimeException("Timeout finding webelement "+ by.toString() +" PLS CHECK report.xls for screen captured");
		  	}
		    try {
		    	Navigator.explicitWait(1000);
			  	size = InteractionUtils.getHowManyByPresntInPage(driver,by,false);
		      } catch ( StaleElementReferenceException ser ) {	
		    	  System.out.println("waitForElementLoad: "+ ser.getMessage() );
		      } catch ( NoSuchElementException nse ) {	
		    	  System.out.println( "waitForElementLoad: "+ nse.getMessage() );
		      } catch ( Exception e ) {
		    	  System.out.println("waitForElementLoad: "+  e.getMessage() );
		      }
		 }
	}
	/**Wait for javascript Ajax to finish at back-end in browser. 
	 * 
	 * @param driver
	 * @param by
	 */
	public static void waitForAjax(WebDriver driver, final By by)  {
		int timeoutInSeconds =Integer.parseInt(Config.getInstance().getProperty("WaitAjaxElment_millis", driver))/1000;
		 // System.out.println("Checking active ajax calls by calling jquery.active");	
		waitForPageLoad(driver);
	    if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor jsDriver = (JavascriptExecutor)driver;
					
			waitForJStoLoad(driver);
	        // after finish loading Ajax Element, then look for it
	        waitForElementLoad(driver, by);
	        //waitForElementShown(driver, by);
		}
		else {
			System.out.println("Web driver: " + driver + " cannot execute javascript");
			waitForElementLoad(driver, by);
		}
	    waitElementToDisplay(driver, by);
	}
	public static void waitForElementShown(WebDriver driver, final By by){
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(by));
	}
	/**Wait until page is fully loaded when switching windows. And FirefoxWebDriver has implemented automatically 
	 * 
	 * @param driver
	 */
	public static void waitForPageLoad(WebDriver driver){
		if (Config.getInstance().getProperty("TragetRunner").equalsIgnoreCase(Type.FireFox.toString())){
			explicitWait(5000);
		}
		
		explicitWait(200);// wait for browser to init. the previous action. 
		waitForJStoLoad(driver);
	}
	
	/**Disable JQuery Animation to speed up execution and make execution more stable
	 * 
	 * @param driver
	 */
	public static void disableJQueryAnimation(WebDriver driver){
		((JavascriptExecutor)driver).executeScript("jQuery.fx.off=true");
	}
	
	public static boolean waitForJStoLoad(WebDriver driver) {

		WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(Config.getInstance().getProperty("WaitAjaxElment_millis", driver))/1000);

		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					Long i=(Long)((JavascriptExecutor)driver).executeScript("return jQuery.active");
					return (i== 0);
				}
				catch (Exception e) {
					return true;
				}
			}
		};

		// wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				String status =((JavascriptExecutor)driver).executeScript("return document.readyState").toString();
				return status.equals("complete");
				}
			};

		return wait.until(jQueryLoad) && wait.until(jsLoad);
		}

}

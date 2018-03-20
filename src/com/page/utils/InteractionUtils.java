package com.page.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.config.Config;
import com.java.utils.DateTimeUtils;
import com.java.utils.URLUtils;
import com.junit.JUnitAssert;
import com.page.navigate.Navigator;


/**
 * 
 * @author lester.li
 * 
 */
public class InteractionUtils {

	// each webdriver has its own visitedWins 	
	public static HashMap<WebDriver, ArrayList<String>> visitedWinsMap = new HashMap<WebDriver, ArrayList<String>>();
	//private final static Lock interaction_LOCK = new ReentrantLock();
	
	public static HashMap<WebDriver, ArrayList<String>> getVisitedWinsMap() {
		return visitedWinsMap;
	}

	public static void setVisitedWinsMap(HashMap<WebDriver, ArrayList<String>> visitedWinsMap) {
		InteractionUtils.visitedWinsMap = visitedWinsMap;
	}



	// Suppress default constructor for noninstantiability
	private InteractionUtils() {

		throw new AssertionError();
	}

//	public static WebDriver getWebDriver_existing() {
//		return driver;
//	}

	/**
	 * 
	 * @param driver
	 * @param text
	 *            check the page text after page load ATTENTION: If Java script
	 *            effect message then need to add wait before call the method to
	 *            check
	 * @return
	 */
	public static boolean textPresentInPage(WebDriver driver, String text) {
		By by = By.xpath("//body");
		Navigator.waitForPageLoad(driver);
		Navigator.explicitWait(1000);
		if (getHowManyByPresntInPage(driver, by, false) > 0) {
			return driver.findElement(by).getText().toLowerCase()
					.contains(text.toLowerCase());
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @param driver
	 * @param text
	 *            check the text present in side a by element after page load
	 *            ATTENTION: If Java script effect message then need to add wait
	 *            before call the method to check
	 * @return
	 */
	public static boolean textPresentInBy(WebDriver driver, By by, String text) {
		Navigator.waitForPageLoad(driver);
		if (getHowManyByPresntInPage(driver, by, true) > 0) {
			return driver.findElement(by).getText().toLowerCase()
					.contains(text.toLowerCase());
		} else {
			return false;
		}

	}
	/**
	 * 
	 * @param driver
	 * @param text
	 *            check the text NOT present in side a by element after page load
	 *            ATTENTION: If Java script effect message then need to add wait
	 *            before call the method to check
	 * @return
	 */
	public static boolean textNotPresentInBy(WebDriver driver, By by, String text) {
		Navigator.waitForPageLoad(driver);
		if (getHowManyByPresntInPage(driver, by, true) > 0) {
			return !(driver.findElement(by).getText().toLowerCase()
					.contains(text.toLowerCase()));
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @param WebDriver
	 *            driver
	 * @param String
	 *            textToFound
	 * @return boolean textPresented
	 */
	public static boolean refreshingAndCheckTextPresentedInPage(
			WebDriver driver, String textToFound) {
		boolean textPresented = false;
		int counter = 0;
		int loop_max = 20;
		while (!textPresented && counter < loop_max) {
			// try 20 times to wait for system auto trigger rule to show out in
			// UI
			driver.navigate().refresh();
			Navigator.explicitWait();
			textPresented = InteractionUtils.textPresentInPage(driver,
					textToFound);
			;
			counter++;
		}
		return textPresented;
	}

	/**
	 * count how many webelement given with "by" object present in same page if
	 * no webelement is found then print out in console
	 * 
	 * @param by
	 * @return
	 */
	private static int getHowManyByPresntInPage(WebDriver driver, By by) {
		// Navigator.explicitWait(1000);
		int size = driver.findElements(by).size();

		if (size == 0) {
			if (Config.PRINTELEMENTNOTFOUNDMSG) {
				System.out.println("warning: cannot find web element:"
						+ by.toString());

			}
		}

		return size;
	}

	/**
	 * count how many webelement given with "by" object present in same page if
	 * no webelement is found then print out in console
	 * 
	 * @param WebDriver
	 *            driver
	 * @param By
	 *            by
	 * @param Boolean
	 *            hasToFindIt, true for by must present; false for no need to
	 *            present
	 * @return
	 */
	public static int getHowManyByPresntInPage(WebDriver driver, By by,
			boolean hasToFindIt) {
		if (hasToFindIt) {
			Navigator.waitForAjax(driver, by);
			return getHowManyByPresntInPage(driver, by);
		} else {
			return getHowManyByPresntInPage(driver, by);
		}

	}
	

	public static void addVisitedWin(WebDriver driver) {
		addVisitedWin(driver, driver.getWindowHandle());
	}

	public static void addVisitedWin(WebDriver driver, String currentWin) {
		ArrayList<String> visitedWins = visitedWinsMap.get(driver);
		if (!visitedWins.contains(currentWin)) {
			visitedWins.add(currentWin);
		}
	}

	public static void switchToNextTab(WebDriver driver) {
		InteractionUtils.switchToPopUpWin(driver);
	}

	public static void switchToPreviousTab(WebDriver driver) {
		InteractionUtils.switchToParentWin(driver);
	}

	public static void switchToPopUpWin(WebDriver driver) {
		int loopCounter = 0;
		do {
			Navigator.explicitWait();
//			if (loopCounter > 30) {
//				throw new RuntimeException(
//						"Time out waiting Pop Up browser in switchToPopUpWin()");
//			}
//			loopCounter = loopCounter + 1;
		} while (driver.getWindowHandles().size() == 1);

		Set<String> wins = driver.getWindowHandles();
		ArrayList<String> visitedWins = visitedWinsMap.get(driver);
		wins.removeAll(visitedWins);// remove the visited pop up windows, but
									// not the newly pop up window
		String[] wins_temp = wins.toArray(new String[0]);
		if (wins_temp.length == 1) {
			String currentWin = wins_temp[0];
			driver.switchTo().window(currentWin);
			Navigator.waitForPageLoad(driver);
			visitedWins.add(currentWin);
		}
	}

	public static boolean hasPopUpWin(WebDriver driver) {
		boolean hasPopUpWin = false;
		Set<String> wins = driver.getWindowHandles();
		ArrayList<String> visitedWins = visitedWinsMap.get(driver);
		wins.removeAll(visitedWins);
		String[] wins_temp = wins.toArray(new String[0]);
		if (wins_temp.length == 1) {
			hasPopUpWin = true;
		}

		return hasPopUpWin;
	}

	public static void switchToParentWin(WebDriver driver) {
		ArrayList<String> visitedWins = visitedWinsMap.get(driver);
		int size = visitedWins.size();
		visitedWins.remove(size - 1);// remove current pop up window
		if (size - 2 < 0) {
			// reaching this code means closing parent windows more than it has
			driver.switchTo().window(visitedWins.get(0));
			Navigator.waitForPageLoad(driver);
		} else {
			driver.switchTo().window(visitedWins.get(size - 2));// return to the
																// parent window
			Navigator.waitForPageLoad(driver);
		}
	}

	/**
	 * Switch to the first window but not close pop-up window
	 * 
	 * @param driver
	 */
	public static void switchToBaseWin(WebDriver driver) {
		String currentWin = driver.getWindowHandle();
		clearVisitedWins(driver);
		addVisitedWin(driver, currentWin);
		driver.switchTo().window(currentWin); // switch to the current window
	}

	public static void clearVisitedWins(WebDriver driver) {
		ArrayList<String> visitedWins = visitedWinsMap.get(driver);
		visitedWins.clear();
	}

	/**
	 * Switch to a iframe in the window
	 * 
	 * @param driver
	 */
	public static void switchToiFrame(WebDriver driver) {
		By frame = By.tagName("iframe");
		driver.switchTo().defaultContent();
		Navigator.waitForAjax(driver, frame);		
		WebElement we = driver.findElement(frame);
		driver.switchTo().frame(we);
		Navigator.waitForPageLoad(driver);
	}

	public static void switchToFrame(WebDriver driver, String frameID) {
		By frame = By.id(frameID);
		driver.switchTo().defaultContent();
		Navigator.waitForAjax(driver, frame);
		WebDriverWait wait = new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameID));
		Navigator.waitForPageLoad(driver);
	}

	public static void switchToNestedFrame(WebDriver driver, ArrayList<By> bys) {
		driver.switchTo().defaultContent();

		int size = -1;
		for (By by : bys) {
			size = getHowManyByPresntInPage(driver, by, false);
			if (size == 1) {
				WebElement we = driver.findElement(by);
				driver.switchTo().frame(we);
			}
		}
	}

	/**
	 * Close all windows and tear down web driver
	 * 
	 * @param driver
	 */
	public static void closeAllWins(WebDriver driver) {
		Set<String> wins = driver.getWindowHandles();
		String[] wins_temp = wins.toArray(new String[0]);

		for (int i = wins_temp.length - 1; i > -1; i--) {
			String currentWin = wins_temp[i];
			driver.switchTo().window(currentWin);
			driver.close();
		}

		InteractionUtils.clearVisitedWins(driver);
	}

	/**
	 * Close all pop-up windows and switch to the base(first) window
	 * 
	 * @param driver
	 */
	public static void closeAllPopUpWins(WebDriver driver) {
		Set<String> wins = driver.getWindowHandles();
		ArrayList<String> visitedWins = visitedWinsMap.get(driver);
		String[] wins_temp = wins.toArray(new String[0]);
		if (visitedWins.size() > 0) {
			for (int i = 0; i < wins_temp.length; i++) {
				String currentWin = wins_temp[i];
				if (!currentWin.equals(visitedWins.get(0))) {
					driver.switchTo().window(currentWin);
					Navigator.waitForPageLoad(driver);
					try{
						driver.close();
					}catch(NoSuchWindowException e){
						System.out.println("Window may be closed so belowing exception happen");
						System.out.println(e.getStackTrace());
					}
				}
			}
			driver.switchTo().window(visitedWins.get(0));
			Navigator.waitForPageLoad(driver);
			clearVisitedWins(driver);
			addVisitedWin(driver);
		} else {
			InteractionUtils.switchToBaseWin(driver);
		}
	}

	/**
	 * Apply to select catalog or organization.
	 * 
	 * @param driver
	 * @param keywords
	 */
	public static void checkMulti_InTree(WebDriver driver, String[] keywords) {
		By by = By.linkText(Config.getInstance().getProperty("link.ExpandTree", driver));
		InteractionUtils.clickLink(driver, by);

		for (String keyword : keywords) {
			InteractionUtils.selectCheckBox_InTree(driver, keyword);
		}

		by = By.name("save");
		InteractionUtils.clickButton(driver, by);
	}

	private static void selectCheckBox_InTree(WebDriver driver,
			String keyword) {
		By by = null;

		if (!keyword.equals("")) {
			String xpath = "";

			if (!keyword.contains("/")) {
				/*
				 * xpath = "//tr[descendant::td[contains(text(),'" + keyword +
				 * "')]]/td/input[@type='CHECKBOX'][1]";
				 */

				xpath = "//tr[descendant::td[text()='" + keyword.trim()
						+ "']]/td/input[@type='CHECKBOX' or @type='RADIO'][1]";

				by = By.xpath(xpath);
				check_checkbox(driver, by);
			} else {
				String[] keywords = keyword.split("/");
				String str = keywords[keywords.length - 1];
				InteractionUtils.selectCheckBox_InTree(driver, str);
			}
		}
	}

	private static void selectRadio_InTree(WebDriver driver,
			String keyword) {
		By by = null;

		if (!keyword.equals("")) {
			String xpath = "";

			if (!keyword.contains("/")) {
				/*
				 * xpath = "//tr[descendant::td[contains(text(),'" + keyword +
				 * "')]]/td/input[@type='CHECKBOX'][1]";
				 */

				xpath = "//tr[descendant::td[text()='" + keyword
						+ "']]/td/input[@type='RADIO' or @type='CHECKBOX'][1]";

				by = By.xpath(xpath);
				check_checkbox(driver, by);
			} else {
				String[] keywords = keyword.split("/");
				String str = keywords[keywords.length - 1];
				InteractionUtils.selectCheckBox_InTree(driver, str);
			}
		}
	}

	/**
	 * Apply to choose catalog and org
	 * 
	 * @param driver
	 * @param keyword
	 */
	public static void checkSingleCheckBox_InTree(WebDriver driver, String keyword) {
		By by = By
				.linkText(Config.getInstance().getProperty("link.ExpandTree", driver));
		InteractionUtils.clickLink(driver, by);

		selectCheckBox_InTree(driver, keyword);
		by = By.name("save");
		InteractionUtils.clickButton(driver, by);
	}

	/**
	 * Use to select org and catalog
	 * 
	 * @param driver
	 * @param keyword
	 */
	public static void checkSingleRadio_InTree(WebDriver driver, String keyword) {
		By by = By
				.linkText(Config.getInstance().getProperty("link.ExpandTree", driver));
		InteractionUtils.clickLink(driver, by);

		selectRadio_InTree(driver, keyword);
		by = By.name("save");
		InteractionUtils.clickButton(driver, by);
	}


	/**
	 * Apply to choose start date and end date
	 * 
	 * @param driver
	 * @param dateString
	 * @param xpath_calendar
	 */
	public static void dateSelect_Calandar(WebDriver driver, String dateString,
			String xpath_calendar) {
		By by = By.xpath(xpath_calendar);
		InteractionUtils.dateSelect_Calandar(driver, dateString, by);
	}
	
	public static void dateSelect_Calandar(WebDriver driver, String dateString, By calendarBy) {

		if (!dateString.equals("")) {
			Calendar cal = DateTimeUtils.strToCalendarDate(dateString);

			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1; // the months are numbered
														// from 0 (January) to
														// 11 (December).
			int day = cal.get(Calendar.DAY_OF_MONTH);

			By by = calendarBy;
			InteractionUtils.clickLink(driver, by);

			by = By.xpath("//select[@data-handler='selectYear']");
			int size = getHowManyByPresntInPage(driver, by, false);
			if (size == 1) {
				InteractionUtils.select_selector(driver, by,  Integer.valueOf(year).toString());
			} 

			by = By.xpath("//select[@data-handler='selectMonth']");
			size = getHowManyByPresntInPage(driver, by, false);
			if (size == 1) {
				InteractionUtils.select_selector(driver, by, month - 1); //
			}
			

			by = By.linkText(Integer.valueOf(day).toString());
			size = InteractionUtils.getHowManyByPresntInPage(driver, by, false);
			if (size == 0) {
				by = By.xpath("//a[@class='ui-state-default ui-state-highlight']");
			}
			WebElement we = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].click();", we);
			
		}
	}

	/**
	 * 
	 * @param driver
	 * @param HTML_ID
	 *            The ID in html source code
	 * @param fileName
	 * 
	 *            This method use to do the file upload testing The HTML_ID
	 *            parameter is the file name which are used in textbox as
	 *            browser file chooser
	 */
	public static void importFile_ID(WebDriver driver, String HTML_ID,
			String fileName) {
		String sysPath = "";
		try {
			sysPath = new java.io.File(".").getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		By id= By.id(HTML_ID);
		Navigator.waitForAjax(driver, id);
		driver.findElement(id).sendKeys(sysPath + fileName);

	}

	public static void uploadFile(WebDriver driver, By by, String filePath) {
		String sysPath = "";
		try {
			sysPath = new java.io.File(".").getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Navigator.waitForAjax(driver, by);
		driver.findElement(by).sendKeys(sysPath+filePath);
	}

	public static boolean isElementPresent(WebDriver driver, By by) {
		try {
			Navigator.waitForPageLoad(driver);
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isElementPresent(WebDriver driver, WebElement e) {
		try {
			Navigator.waitForPageLoad(driver);
			if (e.isDisplayed()) {
				return true;
			}
			return false;
		} catch (Exception exception) {
			return false;
		}
	}
	public static boolean elementShouldPresent(WebDriver driver, By by) {
		try {
			Navigator.waitForAjax(driver, by);
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static synchronized void mouseOver(WebDriver driver, By by) {
		
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		WebElement elementToClick = driver.findElement(by);
		int scrollToY = elementToClick.getLocation().y-(driver.manage().window().getSize().getHeight()/2);
		scrollToY = (scrollToY >= 0)?scrollToY:0;
		((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+scrollToY+")");
		Actions action = new Actions(driver);
		action.moveToElement(elementToClick).build().perform();
	}

	public static void check_checkbox(WebDriver driver, By by) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		WebElement elementToClick = driver.findElement(by);
		int scrollToY = elementToClick.getLocation().y-(driver.manage().window().getSize().getHeight()/2);
		scrollToY = (scrollToY >= 0)?scrollToY:0;
		((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+scrollToY+")");
		if (!elementToClick.isSelected()) {
			elementToClick.click();
		}
	}

	public static void uncheck_checkbox(WebDriver driver, By by) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		WebElement elementToClick = driver.findElement(by);
		int scrollToY = elementToClick.getLocation().y-(driver.manage().window().getSize().getHeight()/2);
		scrollToY = (scrollToY >= 0)?scrollToY:0;
		((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+scrollToY+")");

		if (elementToClick.isSelected()) {
			elementToClick.click();
		}
	}

	public static void fillin_textbox(WebDriver driver, By by, String text) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		WebElement element = driver.findElement(by);
		//int scrollToY = elementToClick.getLocation().y-(driver.manage().window().getSize().getHeight()/2);
		//scrollToY = (scrollToY >= 0)?scrollToY:0;
		//((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+scrollToY+")");
		try {
			element.clear();
			Navigator.waitForAjax(driver, by);
			element = driver.findElement(by); // AICC set revision will fail if
												// uncomment this
			element.sendKeys(text);
		} catch (StaleElementReferenceException e) {
			e.printStackTrace();
		}
		element.sendKeys(Keys.TAB);
	}


	public static void fillin_AjaxTextbox(WebDriver driver, By by, String text) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		WebElement element = driver.findElement(by);
		try {
			element.clear();
			element = driver.findElement(by); // AICC set revision will fail if
												// uncomment this
			element.sendKeys(text);
			Navigator.explicitWait(3000);
		} catch (StaleElementReferenceException e) {
			e.printStackTrace();
		}
		element.sendKeys(Keys.ENTER);
	}

	public static void append_textbox(WebDriver driver, By by, String text) {

		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		driver.findElement(by).sendKeys(text);

	}

	public static void select_selector(WebDriver driver, By by, String selected) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		new Select(driver.findElement(by)).selectByVisibleText(selected);
	}

	public static void select_selector_partialTexts(WebDriver driver,
			String name_selector, String selected) {
		By by = By.xpath("//select[@name='" + name_selector
				+ "']//option[contains(text(),'" + selected + "')]");
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		driver.findElement(by).click();
	}

	public static void select_selector(WebDriver driver, By by, int index) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		new Select(driver.findElement(by)).selectByIndex(index);
	}

	public static void checkRadio(WebDriver driver, By by) {
		clickButton(driver, by);
	}
	
	/**
	 * Check radio button group 
	 * @param option: 1 is the first radio button
	 * 
	 * @param driver
	 * @param by
	 */
	public static void checkRadioBtnGrpByPosition(WebDriver driver, By by, int option) {
		List<WebElement> radios = driver.findElements(by);
		if (option > 0 && option <= radios.size()) {
			radios.get(option - 1).click();
		} else {
			throw new NotFoundException("radion btn option " + option
					+ " not found");
		}
	}
	
	/**
	 * Check radio button group 
	 * @param optionValue: is the first radio button value
	 * 
	 * @param driver
	 * @param by
	 */
	public static void checkRadioBtnGrpByValue(WebDriver driver, By by, String optionValue) {
		List<WebElement> radios = driver.findElements(by);
		int option=0;
		boolean foundMatch=false;
		do{
			String value =radios.get(option).getAttribute("value");
			if (value.equalsIgnoreCase(optionValue.trim())){
				foundMatch=true;
				radios.get(option).click();
			}
			option++;
		}while (option > 0 && option < radios.size());
	
		if(!foundMatch) {
			throw new NotFoundException("radion btn option " + option
					+ " not found");
		}
	}

	
	
	/**
	 * Click the button and then wait for current page to finish loading
	 * 
	 * @param driver
	 * @param by
	 */
	public static synchronized void clickButton(WebDriver driver, By by) {
		Navigator.waitForAjax(driver, by);
		//Actions action = new Actions(driver);
	    //action.sendKeys(Keys.PAGE_DOWN);
		InteractionUtils.highlightElement(driver, by);
		WebElement elementToClick = driver.findElement(by);
		int scrollToY = elementToClick.getLocation().y-(driver.manage().window().getSize().getHeight()/2);
		scrollToY = (scrollToY >= 0)?scrollToY:0;
		// Scroll the browser to the element's Y position 
		((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+scrollToY+")");
		// The scrolling movement causes mouse over event being triggered, using following page action to try if it can fix it.
		//Navigator.waitForJStoLoad(driver);
		elementToClick.click();
		if (hasPopUpWin(driver)) {
			Navigator.waitForPageLoad(driver);
			// below are introduce as the sesssin link in catalog edtior is
			// always hang
			
		}
	}

	public static String getTableRowText(WebDriver driver, By by_table,
			int rowIndex) {
		String rowText = "";
		int size = driver.findElements(by_table).size();
		if (size == 1) {
			By by = By.tagName("tr");
			InteractionUtils.highlightElement(driver, by);
			rowText = driver.findElement(by_table).findElements(by)
					.get(rowIndex).getText();
		}
		return rowText;
	}

	public static int getTableRowCount(WebDriver driver, By by_rows) {
		int size = driver.findElements(by_rows).size();
		if (size >= 1) {
			InteractionUtils.highlightElement(driver, by_rows);
		}
		return size;
	}

	public static int getTableColumnCount(WebDriver driver, By by_columns) {
		int size = driver.findElements(by_columns).size();
		if (size >= 1) {
			InteractionUtils.highlightElement(driver, by_columns);
		}
		return size;
	}

	public static void clickLink(WebDriver driver, By by) {
		clickButton(driver, by);
	}

	public static String getText(WebDriver driver, By by) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		return driver.findElement(by).getText().trim();
	}
	
	public static String getInnerHtml(WebDriver driver, By by) {
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		return driver.findElement(by).getAttribute("innerHTML");
	}

	public static String getValue(WebDriver driver, By by){
		Navigator.waitForAjax(driver, by);
		InteractionUtils.highlightElement(driver, by);
		return driver.findElement(by).getAttribute("value");
	}
	/**
	 * Get the value for a given attribute
	 * 
	 * @param driver
	 * @param by
	 * @param attr
	 * @return
	 */
	public static String getElementAttribute(WebDriver driver, By by, String attr) {
		String result = "";
		int size = getHowManyByPresntInPage(driver, by, false);
		if (size > 0) {
			InteractionUtils.highlightElement(driver, by);
			result = driver.findElement(by).getAttribute(attr);
		}

		return result;
	}

	public static String closeAlertAndGetItsText(WebDriver driver) {

		String alertText = "Cannot get the pop up alert text, pls check it@WebDriverUtils.closeAlertAndGetItsText";

		try {
			Alert javascriptAlert = driver.switchTo().alert();
			alertText = javascriptAlert.getText(); // Get text on alert box

			javascriptAlert.accept(); // click OK

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return alertText;
	}

	public static void cancelAlertIfPresent(WebDriver driver){
		
		boolean exist = InteractionUtils.isAlertPresent(driver);
		if (exist) {
			driver.switchTo().alert().dismiss();
		}
		
	}
	
	// this is for cancelAlert
	public static String cancelAlertAndGetItsText(WebDriver driver) {

		String alertText = "Cannot get the pop up alert text, pls check it@WebDriverUtils.cancelAlertAndGetItsText";

		try {
			Alert javascriptAlert = driver.switchTo().alert();

			alertText = javascriptAlert.getText(); // Get text on alert box
			javascriptAlert.dismiss();// click cancel

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return alertText;
	}

	public static boolean isAlertPresent(WebDriver driver) {

		boolean present = false;
		try {
			// 1. Solution 1: work
			driver.switchTo().alert();
			present = true;

			// 2. Solution 2: not work
			/*
			 * driver.getTitle(); present = true;
			 */
		} catch (Exception e) {
			/*
			 * if (Config.DEBUG_MODE) { System.out.println(e); }
			 */
			present = false;
		}
		return present;
	}

	public static void acceptAlertIfPresent(WebDriver driver) {
		boolean exist = InteractionUtils.isAlertPresent(driver);
		if (exist) {
			acceptAlert(driver);
		}
	}

	public static void acceptAlert(WebDriver driver) {
		driver.switchTo().alert().accept();
		;
	}

	/**
	 * Wait for alert to occur and accept it
	 * 
	 * @param driver
	 */
	public static void waitAlertAndAccept(WebDriver driver) {
		boolean flag = false;
		int loop_max = 10, counter = 0;
		
		do {
			Navigator.waitForPageLoad(driver);
			Navigator.explicitWait(3000);
			driver.navigate().refresh();
			flag = InteractionUtils.isAlertPresent(driver);
			counter++;
		} while (!flag && counter < loop_max);

		if (flag) {
			InteractionUtils.acceptAlert(driver);
		}
	}

	public static void refreshWindow(WebDriver driver) {
		driver.navigate().refresh();
		Navigator.waitForPageLoad(driver);
	}

	public static void mouseUp(WebDriver driver, By by) {
		WebElement we = driver.findElement(by);
		Locatable hoverItem = (Locatable) we;
		Mouse mouse = ((HasInputDevices) driver).getMouse();
		mouse.mouseUp(hoverItem.getCoordinates());
	}

	public static void mouseDown(WebDriver driver, By by) {
		WebElement we = driver.findElement(by);
		Locatable hoverItem = (Locatable) we;
		Mouse mouse = ((HasInputDevices) driver).getMouse();
		mouse.mouseDown(hoverItem.getCoordinates());
	}

	/**
	 * Click at a web element at (x, y) coordinate
	 * 
	 * @param driver
	 * @param by
	 * @param xOffset
	 * @param yOffset
	 */
	public static void clickAt(WebDriver driver, By by, int xOffset, int yOffset) {
		Actions builder = new Actions(driver);
		WebElement toElement = driver.findElement(by);
		builder.moveToElement(toElement, xOffset, yOffset).build().perform();
	}

	/**
	 * Click at a web element at (x, y) coordinate
	 * 
	 * @param driver
	 * @param by
	 * @param xOffset
	 * @param yOffset
	 */
	public static void clickAt(WebDriver driver, By by, String xOffset,
			String yOffset) {
		clickAt(driver, by, Integer.parseInt(xOffset),
				Integer.parseInt(yOffset));
	}

	/**
	 * Mouse down an elment at (x,y) coordinate
	 * 
	 * @param driver
	 * @param by
	 * @param xOffset
	 * @param yOffset
	 */
	private static void mouseDownAt(WebDriver driver, By by, int xOffset,
			int yOffset) {
		Actions builder = new Actions(driver);
		WebElement toElement = driver.findElement(by);
		builder.keyDown(Keys.CONTROL).click(toElement)
				.moveByOffset(xOffset, yOffset).click().build().perform();
	}

	/**
	 * Use JavaScript to click hidden elements
	 * However, it can also handle the gear button menu items clicking
	 * 
	 * @param driver
	 * @param by
	 */
	public static void clickHiddenElement(WebDriver driver, By by) {
		Navigator.waitForAjax(driver, by);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click()", driver.findElement(by));
	}
	

	/**
	 * Use JavaScript to click hidden elements
	 * However, it can also handle the gear button menu items clicking
	 * 
	 * @param driver
	 * @param by
	 */
	public static void clickHiddenElement(WebDriver driver, WebElement elemt) {
		WebDriverWait wait = new WebDriverWait(driver,
				Integer.valueOf(60));
		wait.until(new ProxyWebElementLocated(elemt));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click()", elemt);
	}


	/**
	 * Pre-condition:need to make both src and target elements into view
	 * 
	 * @param driver
	 * @param src
	 * @param target
	 */
	public synchronized static void dragAndDrop(WebDriver driver, By src, By target) {
		Actions builder = new Actions(driver);
		Navigator.waitForAjax(driver, src);
		Navigator.waitForAjax(driver, target);
		WebElement srcEle = driver.findElement(src);
		WebElement destEle = driver.findElement(target);
		// builder.keyDown(Keys.CONTROL).click(srcEle).click(destEle).keyUp(Keys.CONTROL);
		// //Method 1
		builder.clickAndHold(srcEle).moveToElement(destEle).release(destEle)
				.build().perform(); // Method 2
		// builder.dragAndDrop(srcEle, destEle).build().perform(); //Method 3
	}

	/**
	 * Focus on web element
	 * 
	 * @param driver
	 * @param target
	 */
	public static void getFocus(WebDriver driver, By target) {
		WebElement targetEle = driver.findElement(target);
		if (targetEle.getTagName().equals("input")) {
			targetEle.sendKeys("");
		} else {
			Actions builder = new Actions(driver);
			builder.moveToElement(targetEle).perform();
		}

	}

	/**
	 * Scroll window automatically to specific web element
	 * 
	 * @param driver
	 * @param target
	 */
	public static void scrollWindowToElement(WebDriver driver, By target) {
		Navigator.waitForAjax(driver, target);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();",
				driver.findElement(target));
		js.executeScript("window.scrollBy(0,-60)", "");
	}
	
	/**
	 * Scroll window automatically to specific web element
	 * 
	 * @param driver
	 * @param target
	 */
	public static void scrollWindowToElement(WebDriver driver, WebElement webelement) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();", webelement);
		js.executeScript("window.scrollBy(0,-60)", "");
	}

	public static void scrollWindowToElementAt(WebDriver driver, By target,
			int xOffset) {
		Navigator.waitForAjax(driver, target);
		Point hoverItem = driver.findElement(target).getLocation();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0," + (hoverItem.getX() + xOffset)
				+ ");");
	}

	public static void maxWindow(WebDriver driver) {
		driver.manage().window().maximize();
	}

	public static void highlightElement(WebDriver driver, By by) {
		if (Config.getInstance().enableHighlighter) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement elem = driver.findElement(by);
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);", elem,
					"color: red; border: 2px solid red;");
			elem = driver.findElement(by);
			String time = Config.getInstance().getProperty(
					"HighlightElement_millis", driver);
			Navigator.explicitWait(Integer.parseInt(time));
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);", elem,
					"");
		}
	}

	public static void openURL(WebDriver driver, String url) {
		driver.get(url);
		addVisitedWin(driver);
		// WebDriverUtils.checkEKPError(driver);
	}

	/**
	 * 
	 * @param driver
	 * @param srcAdobe
	 * @param key
	 * @param expectedResult
	 */
	public static void checkAdobeFlashResults(WebDriver driver, By srcBy,
			String key, String expectedResult) {
		// 1. Interact with Adobe Flash to check poll statistics
		String temp = URLUtils.decodeURL(InteractionUtils.getElementAttribute(driver,
				srcBy, "value"));
		int index = temp.indexOf("=");
		String dataURL = temp.substring(index + 1);
		String url = "http://" + Config.getInstance().getProperty("IP", driver) + ":"
				+ Config.getInstance().getProperty("port", driver) + dataURL;
		InteractionUtils.openURL(driver, url);

		// 2. Check actual result
		By by = By.xpath("//set[@name='" + key + "']");
		String actualStatistic = InteractionUtils.getElementAttribute(driver, by,
				"value");
		JUnitAssert.assertTrue(expectedResult.contains(actualStatistic),
				expectedResult + " not contain:" + actualStatistic); // 25% vs.
																		// 25
	}

	private static HashMap<String, Integer> getLevels(WebDriver driver, String[] keys) {
		HashMap<String, Integer> key_level = new HashMap();

		for (String key : keys) {
			int level = -1;
			By by = By.xpath("//categories/category");
			String key_temp = InteractionUtils.getElementAttribute(driver, by, "name");
			if (key_temp.equals(key)) {
				level = 1;
			} else {
				by = By.xpath("//categories/category/category");
				key_temp = InteractionUtils.getElementAttribute(driver, by, "name");
				if (key_temp.equals(key)) {
					level = 2;
				} else {
					by = By.xpath("//categories/category/category/category");
					key_temp = InteractionUtils.getElementAttribute(driver, by, "name");
					if (key_temp.equals(key)) {
						level = 3;
					} else {
						by = By.xpath("//categories/category/category/category/category");
						key_temp = InteractionUtils.getElementAttribute(driver, by,
								"name");
						if (key_temp.equals(key)) {
							level = 4;
						}
					}
				}
			}

			key_level.put(key, level);
		}

		return key_level;
	}

	public static void checkAdobeFlashResults(WebDriver driver, By srcBy,
			String[] keys, String[] expectedResults) {
		// 1. Interact with Adobe Flash to check poll statistics
		String temp = URLUtils.decodeURL(InteractionUtils.getElementAttribute(driver,
				srcBy, "value"));
		int index = temp.indexOf("=");
		String dataURL = temp.substring(index + 1);
		String url = "http://" + Config.getInstance().getProperty("IP", driver) + ":"
				+ Config.getInstance().getProperty("port", driver) + dataURL;
		InteractionUtils.openURL(driver, url);
		// System.out.println("url="+url);

		// 2. Check actual result
		int counter = 0;
		for (String expectedResult : expectedResults) {
			String key = keys[counter];
			By by = By.xpath("//set[@name='" + key + "']");
			String actualStatistic = InteractionUtils.getElementAttribute(driver, by,
					"value");
			JUnitAssert.assertTrue(expectedResult.contains(actualStatistic),
					expectedResult + " not contain:" + actualStatistic); // 25%
																			// vs.
																			// 25
			counter++;
		}
	}

	public static void doKeyEventOnBy(WebDriver driver, By by,
			String text, String event) {
		Navigator.waitForAjax(driver, by);
		WebElement element = driver.findElement(by);

		try {
			element.sendKeys(text);
			switch (event) {
			case "keyup":
				new Actions(driver).keyDown(element, Keys.CONTROL)
						.keyUp(element, Keys.CONTROL).perform();
				break;
			case "onblurJS":
				doJavascriptOnBy(driver, by,
						element.getAttribute("onblur"));
				break;
			case "onfocus":
				element.click();
				break;
			case "keyupJS":
				doJavascriptOnBy(driver, by,
						element.getAttribute("onkeyup"));
				break;
			case "keyupTAB":
				element.sendKeys(Keys.TAB);
				break;

			}
		} catch (Exception e) {

		}

	}

	public static void doJavascriptOnBy(WebDriver driver,
			 By by, String javascript) throws Exception {
		Navigator.waitForAjax(driver, by);
		WebElement element = driver.findElement(by);
		((JavascriptExecutor) driver).executeScript(javascript, element);

	}

}

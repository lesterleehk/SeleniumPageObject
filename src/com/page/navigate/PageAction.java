package com.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.python.antlr.PythonParser.return_stmt_return;

import com.java.utils.StringUtils;
import com.junit.JUnitAssert;
import com.page.navigate.HTMLElementManager.ByType;
import com.page.navigate.HTMLElementManager.HTMLElement;
import com.page.utils.InteractionUtils;

public class PageAction {
	
	public final static HTMLElementManager HTML_ELEMENT_MGR= HTMLElementManager.getInstance();
	
	
	public static HTMLElementManager getHtmlElementMgr() {
		return HTML_ELEMENT_MGR;
	}


	private static By getBy(ByType byType, String byValue) {
		By by = null;
		//ClassName, CssSelector, Id, LinkText, Name, PartialLinkText, TagName, XPath, Other
		switch (byType) {
			case ClassName:
				by = By.className(byValue);
				break;
			
			case CssSelector:
				by =By.cssSelector(byValue);
				break;
			
			case Id:
				by =By.id(byValue);
				break;
			
			case LinkText:
				by =By.linkText(byValue);
				break;
			
			case Name:
				by =By.name(byValue);
				break;
			
			case PartialLinkText:
				by =By.partialLinkText(byValue);
				break;
			
			case TagName:
				by =By.tagName(byValue);
				break;
			
			case XPath:
				by =By.xpath(byValue);
				break;
			
			default:
				System.err.println("not supported element in getBy");
				throw new RuntimeException("not supported element in getBy");
		}
		return by;
	}
	
	public static By getByFromCSV(WebDriver driver,String elementName, String[] parameters){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		if (parameters!=null) element.setParameters(parameters);
		return getBy(element.getByType(),element.getByValue());
	}
	
	public static By getByFromCSV(WebDriver driver,String elementName, String parameter){
		return getByFromCSV(driver, elementName, new String[] {parameter});
	}
	
	public static By getByFromCSV(WebDriver driver,String elementName){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		return getBy(element.getByType(),element.getByValue());
	}
	
	/**
	 * 
	 * @param driver
	 * @param elementName
	 */
	public static void findElementToClick(WebDriver driver,String elementName){
		String[] nullStrings = null;
		findElementToClick(driver, elementName, nullStrings);
	}
	
	public static void findElementToClick(WebDriver driver,String elementName, String parameter){
		findElementToClick(driver, elementName, new String[] {parameter});
	}
	
	/**
	 * 
	 * @param driver
	 * @param elementName
	 * @param parameters  -parameters to replace the ? in the byValue
	 */
	public static void findElementToClick(WebDriver driver,String elementName, String[] parameters){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		if (parameters!=null) element.setParameters(parameters);
		
		By by;
		by = getBy(element.getByType(),element.getByValue());
		//System.out.println(by.toString());
		Navigator.waitForAjax(driver, by);
		
		switch(element.getElementType()){
			case PopUpButton:
				InteractionUtils.scrollWindowToElement(driver, by);
				InteractionUtils.clickButton(driver, by);
				InteractionUtils.switchToPopUpWin(driver);
				break;
			case Link:
				InteractionUtils.scrollWindowToElement(driver, by);
				InteractionUtils.clickLink(driver, by);
				break;
			case Button:
				InteractionUtils.scrollWindowToElement(driver, by);
				InteractionUtils.clickButton(driver, by);
				break;
			case RadioButton:
				InteractionUtils.scrollWindowToElement(driver, by);
				InteractionUtils.clickButton(driver, by);
				break;
			case DropDownGearButton:
				// Only use for gearbutton pop up layer menu items 
				InteractionUtils.scrollWindowToElement(driver, by);
				try{
					InteractionUtils.clickLink(driver, by);
				}catch(WebDriverException e){
					try{
						System.out.println("\tElement Blocked\n\tTry to fix problem by mouseover the logout button and retry");
						By Bylogoutbtn = By.xpath("//a[@class='logout' or @class='logout button-no-bg']");
						driver.findElement(Bylogoutbtn);
						InteractionUtils.mouseOver(driver,Bylogoutbtn);
					}catch(NoSuchElementException exceptionSkipped){
						//original element to be clicked is NOT blocked by the overlay drop-down-list of the gear button
						System.out.println("\tlogout button not found");
					}
					InteractionUtils.scrollWindowToElement(driver, by);
					InteractionUtils.clickLink(driver, by);
				}
				break;
			case HiddenElement:
				// Only use for gearbutton pop up layer menu items 
				InteractionUtils.clickHiddenElement(driver, by);
				break;
			case TextBox:
				InteractionUtils.scrollWindowToElement(driver, by);
				InteractionUtils.clickLink(driver, by);
				break;
			default:
				System.err.println(element.toString()+"not supported element in findElementToClick");
				throw new RuntimeException("not supported element in findElementToClick");
			
		}
	
	}
	/**
	 * 
	 * @param driver
	 * @param elementName
	 * @param valueToSet- value to set or select by element
	 */
	public static void findElementToSetValue(WebDriver driver, String elementName, String valueToSet){
		String[] nullStrings = null;
		findElementToSetValue(driver, elementName, nullStrings,valueToSet);
	}
	
	public static void findElementToSetValue(WebDriver driver, String elementName,  String parameter,String valueToSet){
		findElementToSetValue(driver, elementName,  new String[] {parameter},valueToSet);
	}
	
	/**
	 * 
	 * @param driver
	 * @param elementName
	 * @param valueToSet - value to set or select by element
	 * @param parameters -parameters to replace the ? in the byValue
	 */
	public static void findElementToSetValue(WebDriver driver, String elementName,  String[] parameters,String valueToSet){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		if (parameters!=null) element.setParameters(parameters);
		
		By by;
		by = getBy(element.getByType(),element.getByValue());
		Navigator.waitForAjax(driver, by);
		InteractionUtils.scrollWindowToElement(driver, by);
		switch(element.getElementType()){
			case TextBox:
				// passing value to fill in textbox
				InteractionUtils.fillin_textbox(driver, by, valueToSet);
				break;
			case DropDown:
				// passing value for selection
				InteractionUtils.select_selector(driver, by, valueToSet);
				break;
			case RadioBtnGrp:
				// passing value for selection, the value attribute can only be see in HTML code
				//eg. by.name("targetdateOption"), value to set is either SPECIFIC_TARGET_DATE or TARGET_DATE_PERIOD
				//<input id="TARGET_DATE_RADIO_BOX_1" checked="" value="SPECIFIC_TARGET_DATE" class="netd-box-input netd-radiobox" name="targetdateOption" type="RADIO">
				//<input id="TARGET_DATE_RADIO_BOX_2" value="TARGET_DATE_PERIOD" class="netd-box-input netd-radiobox" name="targetdateOption" type="RADIO">
				InteractionUtils.checkRadioBtnGrpByValue(driver, by, valueToSet);
				break;
			case Checkbox:
				// passing value for check the checkbox
				// "enable" is check on the checkbox
				// other then "enable" is uncheck the checkbox
				if (valueToSet.equalsIgnoreCase("enable"))
					InteractionUtils.check_checkbox(driver, by);
				else
					InteractionUtils.uncheck_checkbox(driver, by);
				break;
			case DatePicker:
				InteractionUtils.dateSelect_Calandar(driver, valueToSet, by);
				break;
			
			default:
				System.err.println(element.toString()+"not supported element in findElementToSetValue");
				throw new RuntimeException("not supported element in findElementToSetValue");
			
		}
	
	}
	/**
	 * 
	 * @param driver
	 * @param elementName
	 * @param valueToCheck - use to check the value in the element
	 * @return ture if element value match valueToCheck
	 */
	public static boolean checkTextPresentInElement(WebDriver driver,String elementName, String valueToCheck){
		String[] nullStrings = null;
		return checkTextPresentInElement(driver,elementName,  nullStrings,valueToCheck);
	}
	
	public static boolean checkTextPresentInElement(WebDriver driver,String elementName,String parameter, String valueToCheck ){
		return checkTextPresentInElement(driver, elementName, new String[] {parameter},  valueToCheck); 
	}
	
	/**
	 * 
	 * @param driver
	 * @param elementName
	 * @param valueToCheck - use to check the value in the element
	 * @param parameters -parameters to replace the ? in the byValue
	 * @return ture if element value match valueToCheck
	 */
	public static boolean checkTextPresentInElement(WebDriver driver,String elementName, String[] parameters,String valueToCheck){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);		
		if (parameters!=null) element.setParameters(parameters);
		
		By by;
		by = getBy(element.getByType(),element.getByValue());
		Navigator.waitForAjax(driver, by);
		InteractionUtils.scrollWindowToElement(driver, by);
		String result =InteractionUtils.getText(driver, by).toLowerCase();
		result=StringUtils.replaceBlank(result);
		valueToCheck= StringUtils.replaceBlank(valueToCheck.toLowerCase());
		return result.contains(valueToCheck);
	
	}
	
	
	/**
	 * Wait for SHORT time to check element is presented
	 * @param driver
	 * @param elementName
	 * @return ture if element present
	 */
	public static boolean checkIfElementPresent(WebDriver driver,String elementName){
		String[] nullStrings = null;
		return checkIfElementPresent(driver,elementName, nullStrings);
	}
	
	public static boolean checkIfElementPresent(WebDriver driver,String elementName, String parameter){
		return checkIfElementPresent(driver, elementName, new String[] {parameter});
	}
	
	/**
	 * Wait for SHORT time to check element is presented
	 * @param driver
	 * @param elementName
	 * @param parameters -parameters to replace the ? in the byValue
	 * @return ture if element present
	 */
	public static boolean checkIfElementPresent(WebDriver driver,String elementName, String[] parameters){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		if (parameters!=null) element.setParameters(parameters);
		
		By by;
		by = getBy(element.getByType(),element.getByValue());
		return InteractionUtils.isElementPresent(driver, by);
	}
	
	/**
	 * Wait for LONG time to check element is presented
	 * @param driver
	 * @param elementName
	 * @return ture if element present
	 */
	public static void checkElementShouldPresent(WebDriver driver,String elementName){
		String[] nullStrings = null;
		checkElementShouldPresent(driver,elementName, nullStrings);
	}
	
	public static void checkElementShouldPresent(WebDriver driver,String elementName, String parameter){
		checkElementShouldPresent(driver, elementName, new String[] {parameter});
	}
	
	/**
	 * Wait for LONG time to check element is not presented
	 * @param driver
	 * @param elementName
	 * @param parameters -parameters to replace the ? in the byValue
	 */
	public static void checkElementShouldPresent(WebDriver driver,String elementName, String[] parameters){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		if (parameters!=null) element.setParameters(parameters);
		
		By by;
		by = getBy(element.getByType(),element.getByValue());
		Navigator.waitForAjax(driver, by);
		InteractionUtils.scrollWindowToElement(driver, by);
		JUnitAssert.assertTrue(InteractionUtils.elementShouldPresent(driver, by), by.toString() +" shuold be found");
	}

	/**
	 * Wait for LONG time to check element is not presented
	 * @param driver
	 * @param elementName
	 * @return ture if element present
	 */
	public static void checkElementShouldNotPresent(WebDriver driver,String elementName){
		String[] nullStrings = null;
		checkElementShouldNotPresent(driver,elementName, nullStrings);
	}
	
	public static void checkElementShouldNotPresent(WebDriver driver,String elementName, String parameter){
		checkElementShouldNotPresent(driver, elementName, new String[] {parameter});
	}
	
	/**
	 * Wait for LONG time to check element is not presented
	 * @param driver
	 * @param elementName
	 * @param parameters -parameters to replace the ? in the byValue
	 */
	public static void checkElementShouldNotPresent(WebDriver driver,String elementName, String[] parameters){
		HTMLElement element = HTML_ELEMENT_MGR.getElement(elementName);
		element.replaceLableKey(driver);
		if (parameters!=null) element.setParameters(parameters);
		
		By by;
		by = getBy(element.getByType(),element.getByValue());
	
		JUnitAssert.assertTrue(!InteractionUtils.isElementPresent(driver, by), by.toString() +" should not found");
	}
}

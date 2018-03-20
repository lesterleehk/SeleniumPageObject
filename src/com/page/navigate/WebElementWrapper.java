package com.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.config.Config;
import com.java.utils.URLUtils;

/**
 * 
 * @author lester.li This is a wrapper class for WebElement
 */
public class WebElementWrapper {
	private String seleniumByType;
	private String seleniumByValue;
	private String htmlElementType;

	private String id;
	private By by;
	private String parentId;
	private WebDriver driver;

	//private String expectMsg;
	public static final String ROOT = "ROOT";
	public static final String ByClassName = "CLASSNAME";
	public static final String ByCssSelector = "CSSSELECTOR";
	public static final String ById = "ID";
	public static final String ByLinkText = "LINKTEXT";
	public static final String ByName = "NAME";
	public static final String ByPartialLinkText = "PARTIALLINKTEXT";
	public static final String ByTagName = "TAGNAME";
	public static final String ByXPath = "XPATH";
	public static final String LABLEKEY = "LabelKey";
	public static final String URL = "URL";
	public static final String IsTopMenu = "True";
	public static final String Parameter = "Parameter";
	public static final String HtmlElementType = "HtmlElementType";
	
	private String parameter="";
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public boolean isTopMenu() {
		return topMenu;
	}
	public void setTopMenu(boolean topMenu) {
		this.topMenu = topMenu;
	}
	
	public WebDriver getDriver() {
		return driver;
	}
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	public String getHtmlElementType() {
		return htmlElementType;
	}
	public void setHtmlElementType(String htmlElementType) {
		this.htmlElementType = htmlElementType;
	}
	
	private boolean topMenu=false;
	
	/**
	 * 
	 * @param id
	 *            map to ID column in sheet
	 * @param seleniumByType
	 *            map to type column in sheet
	 * @param value
	 *            map to value column in sheet
	 * @param parentId
	 *            map to parentId column in sheet
	 * @param sheet
	 *            : the sheet hold the WebElement
	 * @throws Exception
	 *             if type is not supported
	 */
	public WebElementWrapper(String htmlElementType, String id, String seleniumByType, String seleniumByValue, String parameter, boolean topMenu,String parentId, WebDriver driver) {
		this.setHtmlElementType(htmlElementType);
		this.setId(id);
		this.setSeleniumByType(seleniumByType);
		this.setSeleniumByValue(seleniumByValue);
		this.setParentId(parentId);
		this.setParameter(parameter);
		this.setTopMenu(topMenu);
		this.setBy(this.constructBy(seleniumByType, seleniumByValue, parameter, topMenu, driver));
		this.setDriver(driver);
		//this.expectMsg=expectMsg;
	}
	
	private By constructBy(String seleniumByType, String seleniumByValue, String parameter, boolean topMenu, WebDriver driver){
		By by = null;
		switch (seleniumByType.toUpperCase()) {
			case URL:
				setSeleniumByValue(URLUtils.URL_Prefix+"/servlet/"+seleniumByValue);
				break;
			case ByClassName:
				by = By.className(seleniumByValue);
				break;
			case ByCssSelector:
				by = By.cssSelector(seleniumByValue);
				break;
			case ById:
				by = By.id(seleniumByValue);
				break;
			case ByLinkText:
				String tempLinkText="";
				tempLinkText=Config.getInstance().getProperty(seleniumByValue, driver);
				if (topMenu)
					by = By.linkText(tempLinkText.toUpperCase());
				else if (tempLinkText.equalsIgnoreCase("Overview")){
					by = By.linkText(tempLinkText.toUpperCase());
				}else{
					by = By.linkText(tempLinkText);
				}	
				break;
			case ByName:
				by = By.name(seleniumByValue);
				break;
			case ByPartialLinkText:
				if(parameter.equals(WebElementWrapper.LABLEKEY)) 
					by = By.partialLinkText(Config.getInstance().getProperty(seleniumByValue, driver));
				else
					By.partialLinkText(seleniumByValue);
				break;
			case ByTagName:
				by = By.tagName(seleniumByValue);
				break;
			case ByXPath:// self customized xpath
				if(parameter.equals(WebElementWrapper.LABLEKEY)) 
				{
					String labelKey=seleniumByValue.split("'")[1]; //example: xpath=//a[descendant::span[text()='menu.org.user.review']]
					String label_text = Config.getInstance().getProperty(labelKey, driver); 
					seleniumByValue = seleniumByValue.replace(labelKey, label_text);
					by = By.xpath(seleniumByValue);
				}else{
					by = By.xpath(seleniumByValue);		
				}
				break;
			default:
				System.out.println("WebElementWrapper does not support such WebElement type="
						+ this.seleniumByType);
				break;
		}

		return by;
	}	
	public String getSeleniumByType() {
		return seleniumByType;
	}
	public void setSeleniumByType(String seleniumByType) {
		this.seleniumByType = seleniumByType;
	}

	public String getSeleniumByValue() {
		if (seleniumByValue.contains("?") && !(seleniumByType.equalsIgnoreCase(URL))){
			throw new RuntimeException("Error>>WebElementWrapper:paramters is not set yet "+seleniumByValue +
			" Pls use setParamter(String[] parameters)  to replace ? before call getElementValue");
        }else{
        	return seleniumByValue;
        }
	}
	
	public void setSeleniumByValue(String elementValue) {
		this.seleniumByValue = elementValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public By getBy() {
		return by;
	}

	public void setBy(By by) {
		this.by = by;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
    /**
     * 
     * @param parameters to replace the "?" in the element value
     */
	public void setParamters (String[] parameters) {
		// check if value has "?" to replace
		if  (this.seleniumByValue.contains("?")){
			String[] splited = this.seleniumByValue.split("[?]");
			if (splited.length ==parameters.length+1){
				this.seleniumByValue="";
				for (int i =0; i<splited.length-1; i++){
					this.seleniumByValue= this.seleniumByValue+splited[i].concat(parameters[i]);
				}
				this.seleniumByValue= this.seleniumByValue+splited[splited.length-1];
			}else{
				System.out.println("Error>>WebElementWrapper: setParamter: parameters size is not matched");
				throw new RuntimeException("Error>>WebElementWrapper: setParamter: parameters size is not matched");
			}
			by = By.xpath(this.seleniumByValue);			
		}else{
			System.out.println("Error>>WebElementWrapper: setParamter: parameters not existed");
			throw new RuntimeException("Error>>WebElementWrapper: setParamter: parameters not existed");
		}
		
	}
	public WebElementWrapper Clone (){
		return new WebElementWrapper(this.htmlElementType,this.getId(), this.getSeleniumByType(), this.seleniumByValue,this.getParameter(), this.isTopMenu(),
					this.getParentId(), this.getDriver());
	}
	
}

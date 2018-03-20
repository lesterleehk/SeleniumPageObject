package com.pageobject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.abstractclasses.TestObject;
import com.java.utils.Validate;
import com.page.navigate.HTMLElementManager.HTMLElement;

public class CatalogEditorModuleProperties extends BasePage{
	


	//String PageURL= "PageURL"; no need for this page, as it has to be access via LearningModule Search
	
	public CatalogEditorModuleProperties(WebDriver driver, TestObject obj ) {
		super(driver,obj);
		File tsvFile = new File(HomePageWithElements.class.getClassLoader().getResource("/elements/CatalogEditorModuleProperties.csv").getFile());
		htmlElements.AddPageElements(tsvFile);
	
	}	
	
	public void GoToSessionProperties() {
		page.findElementToClick("elementid");
		
	}
	// When the element key is same as table key, then the talbe filling can do in this way
	public void FillUpModuleProperties() {
		HashMap<String, HTMLElement> HTMLElementsMap=htmlElements.getHTMLElementsMap();
		Iterator it = HTMLElementsMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        String valuetoFill= testCaseData.getValue(pair.getKey().toString());
	        if (!Validate.isBlank(valuetoFill)) {
	        	page.findElementToSetValue(pair.getKey().toString(), valuetoFill);
	        }
	    }
	}
	public void goToPage() {
		System.out.println("Cannot Directly access the page");
		throw new RuntimeException("Cannot Directly access the CatalogEditorModuleProperties");
	}
	
	


}

package com.page.navigate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.config.Config;
import com.junit.JUnitAssert;

public class HTMLElementManager {
	
	public enum ElementType {
		Link, DropDown, Checkbox, Button, RadioButton, TextBox, DropDownGearButton, RadioBtnGrp, PlainText, DatePicker, HiddenElement, AutoCompleteTextBox, PopUpButton
	}
	
	public enum ByType {
		ClassName, CssSelector, Id, LinkText, Name, PartialLinkText, TagName, XPath
	}
	
	
	
	private static HTMLElementManager instance;
	private HashMap<String, HTMLElement> HTMLElementsMap = new HashMap<String, HTMLElement>(); 
	
	
	
	public HTMLElementManager(String tsvFile) {
		int rowNum;
		File file = new File(tsvFile);
		try {
			Scanner scanner = new Scanner(file);
			//ignore first line
			scanner.nextLine();
			rowNum = 2;
			// start parsing
			while (scanner.hasNextLine()){
				String row = scanner.nextLine();
				String[] cell = row.split("\t");
				boolean lengthValid=(cell.length == 5) ||(cell.length == 6) ;		
				JUnitAssert.assertTrue(lengthValid, "HTMLElements.tsv row "+rowNum +" contains invalid number of elements element, length= " +cell.length+ " detail="+row.toString());
				HTMLElement newElement = new HTMLElement(cell[0], cell[1], cell[2], cell[3], cell[4], rowNum);
				HTMLElementsMap.put(newElement.getElementName(), newElement);
				rowNum++;
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("HTMLElement TSV File not found");
			e.printStackTrace();
		}
	}
	public void AddPageElements(File file) {
		
		
		int rowNum;
		
		try {
			Scanner scanner = new Scanner(file);
			//ignore first line
			scanner.nextLine();
			rowNum = 2;
			// start parsing
			while (scanner.hasNextLine()){
				String row = scanner.nextLine();
				String[] cell = row.split("\t");
				boolean lengthValid=(cell.length == 5) ||(cell.length == 6) ;		
				JUnitAssert.assertTrue(lengthValid, "HTMLElements.tsv row "+rowNum +" contains invalid number of elements element, length= " +cell.length+ " detail="+row.toString());
				HTMLElement newElement = new HTMLElement(cell[0], cell[1], cell[2], cell[3], cell[4], rowNum);
				HTMLElementsMap.put(newElement.getElementName(), newElement);
				rowNum++;
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("HTMLElement TSV File not found");
			e.printStackTrace();
		}
	}
	
	/**
	 * gets the desired HTMLElement by providing the elementName (first column in TSV file)
	 * @param elementName
	 * @return
	 */
	public HashMap<String, HTMLElement> getHTMLElementsMap(){
		return HTMLElementsMap;
	}
	/**
	 * gets the desired HTMLElement by providing the elementName (first column in TSV file)
	 * @param elementName
	 * @return
	 */
	public HTMLElement getElement(String elementName){
		return HTMLElementsMap.get(elementName);
	}
	
	public static synchronized HTMLElementManager getInstance() {
		if(instance == null) {
			String tsvFile = System.getProperty("user.dir")+"\\"+ Config.getInstance().getProperty("HTMLElements");
			instance = new HTMLElementManager(tsvFile);
		}
		return instance;
	}
	
	public class HTMLElement {		
		private final String elementName, forceUpper;
		private final ElementType elementType;
		private final ByType byType;
		private String byValueOriginal, byValue;
		
		
		public HTMLElement(String elementName, String elementTypeStr, String byTypeStr, String byValue, String forceUpper, int rowNum){
			this.elementName = elementName;
			this.byValueOriginal = byValue;
			this.byValue = byValue;
			this.forceUpper = forceUpper;
			
			switch(elementTypeStr.toLowerCase()) {
				case "link": 
					this.elementType = ElementType.Link;
					break;
				case "dropdown": 
					this.elementType = ElementType.DropDown;
					break;
				case "checkbox": 
					this.elementType = ElementType.Checkbox;
					break;
				case "button":
					this.elementType = ElementType.Button;
					break;
				case "radiobutton":
					this.elementType = ElementType.RadioButton;
					break;
				case "textbox":
					this.elementType = ElementType.TextBox;
					break;
				case "dropdowngearbutton":
					this.elementType = ElementType.DropDownGearButton;
					break;
				case "radiobuttongroup":
				case "radiobtngrp":
					this.elementType = ElementType.RadioBtnGrp;
					break;
				case "plaintext":
					this.elementType = ElementType.PlainText;
					break;
				case "datepicker":
					this.elementType = ElementType.DatePicker;
					break;
				case "hiddenelement":
					this.elementType = ElementType.HiddenElement;
					break;
				case "autocompletetextbox":
					this.elementType = ElementType.AutoCompleteTextBox;
					break;
				case "popupbutton":
					this.elementType = ElementType.PopUpButton;
					break;
				default:
					System.err.println("Row="+rowNum+": HTMLElementTSV contains "+elementTypeStr+" element type is not supported");
					throw new RuntimeException("Row="+rowNum+": HTMLElementTSV contains "+elementTypeStr+" element type is not supported");
			}
			
			switch(byTypeStr.toLowerCase()) {
				case "classname":
					this.byType = ByType.ClassName;
					break;
				case "cssselector":
					this.byType = ByType.CssSelector;
					break;
				case "id":
					this.byType = ByType.Id;
					break;
				case "linktext":
					this.byType = ByType.LinkText;
					break;
				case "name":
					this.byType = ByType.Name;
					break;
				case "partiallinktext":
					this.byType = ByType.PartialLinkText;
					break;
				case "tagname":
					this.byType = ByType.TagName;
					break;
				case "xpath":
					this.byType = ByType.XPath;
					break;
				default:
					System.err.println("Row="+rowNum+": HTMLElementTSV contains "+byTypeStr+" by type is not supported");
					throw new RuntimeException("Row="+rowNum+": HTMLElementTSV contains "+byTypeStr+" by type is not supported");
						
			}
		}
		
		public String getElementName() {
			return elementName;
		}

		public ElementType getElementType() {
			return elementType;
		}

		public ByType getByType() {
			return byType;
		}

		public String getByValue() {
			return byValue;
		}
		
		public boolean isForceUppercase() {
			return forceUpper.toLowerCase().startsWith("y");
		}
		
	    /**
	     * 
	     * @param parameters to replace the "?" in the by value
	     */
		public void setParameters(String p[]){
			int count = StringUtils.countMatches(byValueOriginal, "?");
			JUnitAssert.assertTrue(count == p.length, "mismatch in number of parameters");
			String temp=byValueOriginal;
			
			for(int i = 0; i < count; i++){
				this.byValue = temp.replaceFirst("\\?", p[i]);
				temp=this.byValue;
			}
		}
		
		 /**
	     * 
	     * @param replace LableKey to actual value to the by value
	     */
		public void replaceLableKey(WebDriver driver){
			
			synchronized (WebDriver.class) {
				int start =this.byValueOriginal.indexOf("${");
				
				if (start >=0){
					String copy_byValue=this.byValueOriginal.substring(start);
					int end =start+copy_byValue.indexOf("}");
					String labelKey =this.byValueOriginal.substring(start+2, end); //+2 for ${ and -1 for }
					String replacement=Config.getInstance().getProperty(labelKey, driver);
					
					if (replacement.equalsIgnoreCase(labelKey)) {
						System.err.println("CANNOT FIND THE LABEL KEY ="+labelKey +" in " +byValueOriginal);
						throw new RuntimeException("CANNOT FIND THE LABEL KEY ="+labelKey +" in " +byValueOriginal);
					}
					replacement=StringEscapeUtils.unescapeJava(replacement);
					if (isForceUppercase())
						replacement=replacement.toUpperCase();
					this.byValueOriginal = this.byValueOriginal.replace("${"+labelKey+"}", replacement);
					
					//System.out.println("LABEL KEY= "+labelKey+" VALUE ="+byValueOriginal);
					this.byValue = byValueOriginal;
				}
			}
		}
	}
}

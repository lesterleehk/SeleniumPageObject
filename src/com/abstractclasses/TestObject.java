package com.abstractclasses;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.config.Config;
import com.controller.TestDriver;
import com.java.utils.CriteriaParser;
import com.java.utils.POIUtils;
import com.java.utils.Validate;
import com.junit.JUnitAssert;
import com.page.navigate.Navigator;
import com.page.utils.InteractionUtils;

/**Extended by all testing objects (which are defined in "com.netdimen.model" package)
 * 
 * @author lester.li
 *
 */
public abstract class TestObject{
	// id =sheetName+funcType+row.getRowNum()
	@FindBy (xpath="//Body")
	public WebElement pageContent;
	

	protected String UID = "", PWD = "", FuncType = "", ID = "",ExpectedResult = "", TestSuite = "", 
			ObjectInputs = "", TestCaseType="", 
			ImportantCase="", ImportantCase_Suite="", ObjectName="";
	
	public HashMap<String, String> tableValue= new HashMap<String, String>();
	
	public String getValue(String key) {
		return (String)tableValue.get(key.trim().toLowerCase());
	}
	
	public void setValue(String key, String value) {
		tableValue.put(key.trim().toLowerCase(), value);
	}
	
	private TestDriver testDriver;
	
	protected ArrayList<TestObject> testCaseArray = new ArrayList<TestObject>();
	protected ArrayList<TestObject> objectParams = new ArrayList<TestObject>();
	
	
	public String getTestCaseType() {
		return TestCaseType;
	}
	
	public TestObject(){
		this(null);
	}

	public TestObject(TestDriver tdriver){
		UID = Config.getInstance().getProperty("user.admin"); 
		PWD = Config.getInstance().getProperty("user.admin.pass");
		testDriver = tdriver;
	}

	public String getID() {
		return ID;
	}

	// method are call with reflection
	//	POIUtils.field.set(obj,TestObject.genObjectID(sheetName, funcType, row.getRowNum()));
	//
	public void setID(String iD) {
		ID = iD;
	}

	public String toString(){
		return this.ID+"-"+ this.getUID();
	} 
	
	public void setTestCaseType(String testcaseType) {
		TestCaseType = testcaseType;
	}
	
	public TestDriver getTestDriver() {
		return testDriver;
	}

	public void setTestDriver(TestDriver testDriver) {
		this.testDriver = testDriver;
	}

	
	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
		//DBUserDAO dbUserDAO = new DBUserDAO(TestDriver.dbManager.getConn());
		//this.logonDBUser = dbUserDAO.findByUserId(UID.toLowerCase().trim());
	}


	public ArrayList<TestObject> getESignParams() {
		return objectParams;
	}
	
	
	public ArrayList<TestObject> getObjectParams() {
		return objectParams;
	}


	public void setObjectParams(ArrayList<TestObject> objectParams) {
		this.objectParams = objectParams;
	}


	public ArrayList<TestObject> getTestCaseArray() {
		return testCaseArray;
	}


	public void setTestCaseArray(ArrayList<TestObject> testCaseArray) {
		this.testCaseArray = testCaseArray;
	}


	public String getObjectInputs() {
		return ObjectInputs;
	}

	public void setObjectInputs(String str) {
		ObjectInputs = str;
		this.setObjectParams(this.loadTestCases(ObjectInputs));
	}

	public String getTestSuite() {
		return TestSuite;
	}

	public void setTestSuite(String testSuite) {
		TestSuite = testSuite;
		testCaseArray = this.loadTestCases(testSuite);
	}


	/**
	 * 
	 * @param testCasesStr eg: CDC:runDeployGoal_CDC:2\nCDC:runDeployGoal_CDC:3
	 * seperated by "\n" between cases
	 * Chained testobject creation will happen eg. runCheckGoalLock(Goal)->runDeployGoal_CDC:2(CDC)->PerformanceGoal:1(PerformanceGoal)
	 * @return
	 */
	
	public ArrayList<TestObject> loadTestCases(String testCasesStr){
		ArrayList<TestObject> testCaseArray = new ArrayList<TestObject>();
		String[] testCases = testCasesStr.split("\n");

		try {
			HSSFWorkbook wb = new HSSFWorkbook(Config.getInstance().getTestFileInputStream());

			for (String testCase : testCases) {
				String[] testCase_array = testCase.split(":");
				if (testCase_array.length<2)
					throw new RuntimeException("<b>ERROR: loadTestSuite, the case row or method is not filled");
				String sheetName = testCase_array[0].trim();
				ObjectName=sheetName;
				String funcType = testCase_array[1].trim();
				int rowNum = Integer.parseInt(testCase_array[2].trim());
				// try to load the testcase from here for testsuite or objectInputs
				TestObject obj = POIUtils.loadTestCaseFromExcelRow(sheetName,
						funcType, rowNum, wb);
				if (obj == null){
					throw new RuntimeException("<b>ERROR: loadTestSuite In "+this.getFuncType()+"-->CAN NOT Find "+ funcType + " in " +sheetName +" row "+rowNum+"<b/>");
				}else{
					obj.setTestDriver(this.getTestDriver());
					testCaseArray.add(obj);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e){
			testCaseArray= null;
			e.printStackTrace();
		}

		return testCaseArray;
	}	
	
	public boolean equals(TestObject obj){ //compare TestObject
		return false;
	}

	/**Failed if the page contains keyword "error"
	 * 
	 * @param driver
	 * @param expectedResult
	 */
	public void checkExpectedResult_UI(WebDriver driver, String expectedResult){
		String text = "Please contact the system administrator";
		JUnitAssert.assertTrue(!InteractionUtils.textPresentInPage(driver, text), "EKP error was found in test case");
//		WebDriverUtils.checkEKPError(driver);
	} 

	public String getPWD() {
		return PWD;
	}

	public void setPWD(String pWD) {
		PWD = pWD;
	}

	public String getFuncType() {
		return FuncType.trim();
	}

	public void setFuncType(String funcType) {
		FuncType = funcType;
	}


	public void run(WebDriver driver) {

	}

	public void checkVisibilityInBy(WebDriver driver, By by){
		
		HashMap<String, ArrayList<String>> criteria_fields = CriteriaParser
				.parseKeyValueList(":", ";", this.getExpectedResult());

		Iterator<String> criteria = criteria_fields.keySet().iterator();
		while (criteria.hasNext()) {
			String criterion = criteria.next();
			ArrayList<String> fields = criteria_fields.get(criterion);
			for (String field : fields) {
				// check the link present
				switch (criterion.toLowerCase()) {
				case "visible":
					JUnitAssert.assertTrue(InteractionUtils.textPresentInBy(
							driver, by, field), "Should find:"+ field);
					break;
				case "invisible":
					JUnitAssert.assertTrue(!InteractionUtils.textPresentInBy(
							driver, by, field), "Should not find:"+ field);
					break;
				}
			}
		}
	}
	
	public String getExpectedResult() {
		return ExpectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		ExpectedResult = expectedResult;
	}
	public String getImportantCase() {
		return ImportantCase;
	}

	public void setImportantCase(String importantCase) {
		ImportantCase = importantCase.trim();
	}

	public String getImportantCase_Suite() {
		return ImportantCase_Suite;
	}

	public void setImportantCase_Suite(String importantCase_Suite) {
		ImportantCase_Suite = importantCase_Suite.trim();
	}

	public static String genObjectID(String sheetName, int rowIndex){
		return new StringBuilder().
				append(sheetName).
				append("_").
				append(rowIndex).
				toString();
	}
	/**
	 * 
	 * @param sheetName
	 * @param funcType
	 * @param rowIndex 
	 *        excel row is starting from 0, so +1 to represent the actual row for human readable
	 * @return
	 */
	public static String genObjectID(String sheetName, String funcType, int rowIndex){
		return new StringBuilder().
				append(sheetName).
				append("_").
				append(funcType).
				append("_").
				append(rowIndex+1).
				toString();
	}
	/**
	 * 
	 * @param object
	 * @param defaultValue
	 * @return object if object is not null, otherwise return defaultValue
	 */
	public static final <T> T defaultIfNull(final T object, final T defaultValue) {
		if (object == null)
			return defaultValue;
		else 
			return object;
	}
	
	public abstract void initilizeElements(WebDriver driver);
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getALLFields(){
		
		Class<? extends TestObject> clz =this.getClass();
		
		ArrayList<String> list = new ArrayList<String>();
		
		Field f[] =clz.getDeclaredFields();
		
		
		for (int i=0; i<f.length; i++){
			if (f[i].toString().contains("String"))
			list.add(f[i].getName());
		}
		
		Class<? extends Object> superClz;
		do{
			superClz = clz.getSuperclass();
			if(superClz != null){
				Field Superf[]= superClz.getDeclaredFields();
				for (int i=0; i<Superf.length; i++){
					if (Superf[i].toString().contains("String"))
					list.add(Superf[i].getName());
				}
			}
			if (superClz.toString().contains("TestObject"))
				break;
		}while(true);
		
		return list;
	
	}
	public void login(WebDriver driver, String URL, String UID, String PWD) {
		InteractionUtils.openURL(driver, URL);
		By by = By.xpath("(//input[@name='username'])[2]");
		InteractionUtils.fillin_textbox(driver, by, UID);

		by = By.xpath("(//input[@name='password'])[2]");
		InteractionUtils.fillin_textbox(driver, by, PWD);

		by = By.xpath("(//button[@name='loginsubmit'])[2]");
		InteractionUtils.clickButton(driver, by);
		System.out.println("logon btn clicked");
		Navigator.waitForPageLoad(driver);
		//**IMPORTANT need to wait for the login redirect paging process to be finished
		Navigator.explicitWait(3000);
		
	}
	/**
	 * Login as a default user in config.properties
	 * 
	 * @param driver
	 */
	public void login(WebDriver driver) {
		String URL = Config.getInstance().getProperty("loginURL", driver);
		this.login(driver, URL, this.UID, this.PWD);

		// System.out.println("UserLocale:"+
		// Config.getInstance().getUserLocale());
	}

	public void logout(WebDriver driver) {
		InteractionUtils.closeAllPopUpWins(driver);
		Navigator.waitForPageLoad(driver);
		//By by = By.xpath("//a[contains(@href,'logout')]");		
		//InteractionUtils.clickHiddenElement(driver, by);
		
		
		By by =By.xpath("//div[@id='control_pan']");
		//using follow will cause the firefox browser to fail
		//if (InteractionUtils.isElementPresent(driver, by)) {
			InteractionUtils.mouseOver(driver, by);
			by = By.xpath("//div[@id='control_user']//a[contains(@href,'logout')]");		
			InteractionUtils.clickButton(driver, by);
		    Navigator.waitForPageLoad(driver);
		//}

	}


}

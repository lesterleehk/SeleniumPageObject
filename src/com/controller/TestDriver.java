package com.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.abstractclasses.TestObject;
import com.annotation.NetdTestRule;
import com.annotation.TimeLogger;
import com.config.Config;
import com.google.common.base.Throwables;
import com.java.utils.Convert_CSV_XLS;
import com.java.utils.DateTimeUtils;
import com.java.utils.POIUtils;
import com.java.utils.PropertiesFileUtils;
import com.java.utils.ReflectionUtils;
import com.junit.Parallelized;
import com.junit.ScreenShotOnFailed;
import com.junit.TestReport;
import com.page.navigate.PageAction;
import com.page.test.User;


/**
 * Starting point for all test cases. This test driver do the following things
 * in order: 1. Load test cases from Excel: 1.a. read EKPMain page to load
 * sheetName (=test object name, indicate which class should be tested) ->
 * FuncType (= methodName, indicate which method should be tested) pairs 1.b.
 * for each sheet in Step 1.a, filter rows based on FuncType. Each row is mapped
 * into test object via Java Reflection; 1.c. save all test objects into HashMap
 * for further reference 2. Execute test cases: 2.a. For each test object, check
 * whether it needs to switch users 2.b. For each test object, only execute
 * specific method (specified by FuncType); 2.c. If test fails, take screenshot
 * and re-login the system;
 * 
 * @author lester.li
 * @author Ernest.Chui
 * @author lester.li
 * Added support for multi-thread
 *
 */
@RunWith(Parallelized.class)
public class TestDriver {
	private WebDriver driver;
	private TestObject testObject;
	private User currentUser;
	private int thisTestNo;
	private static int numFailCases=0;
    private static int totalTestSuite = 0;
	private static int testSuiteNo = 1;
	private static int totalExecution = 0;
	private static final Map<String, String> actualImportantFail = new ConcurrentHashMap<String, String>();
	//public static DBManager dbManager = new DBManager();
	private static String sheetName_main="AAll_Cases";
	private static boolean someTestCaseIsMissing=false;
	
	private static HashMap<String, TestObject> ID_testObjects = new HashMap<String, TestObject>();

	public TestDriver(TestObject testObject, String objID) {
		this.testObject = testObject;
		testObject.setTestDriver(this);
		User user = new User("QAsuperuser2","zxcvbnm"); // initialized the user as default logon user
		testObject.getTestDriver().currentUser=user;
	}
	
	public WebDriver getWebDriver() {
		return driver;
	}

	public static void addTestObject(String ID, TestObject obj) {
		if (ID_testObjects.containsKey(ID)) {
			if (Config.DEBUG_MODE) {
				System.out.println("Duplicate ID:" + ID);
			}
		} else {
			ID_testObjects.put(ID, obj);
		}
	}
	
    public TestObject getTestObject() {
		return testObject;
	}

	public static TestObject getTestObject(String ID) {
		TestObject obj_tmp = null;
		if (ID_testObjects.containsKey(ID)) {
			obj_tmp = ID_testObjects.get(ID);
		}

		return obj_tmp;
	}

	@BeforeClass
	public static void executedBeforeAllTestCases() throws IOException {
	
	}

	
	@Before
	public void setUp() throws Exception {
		
		driver = TestDriverUtils.acquireWebDriver();
		Runtime.getRuntime().addShutdownHook(new Thread(driver::quit));
	}
	
	private static boolean isPrevTestResultDeleted(){
		try{
    		File file = new File(Config.getInstance().getProperty("test.result"));
    		if (file.exists()){
	    		if(file.delete()){
	    			System.out.println(file.getName() + " is deleted!");
	    			return true;
	    		}else{
	    			System.out.println("Delete "+Config.getInstance().getProperty("test.result")+" operation is failed.");
	    			return false;
	    		}
    		}
    		return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
	}
	@Parameters(name = "{1}") //name = "{1}"=Use TestObject.toString() as test case name
	public static Collection<Object[]> data() throws IOException {
		//Transform each row in excel into java object 
		try {
			Convert_CSV_XLS.GenerateExcel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!isPrevTestResultDeleted()) return null;
		Collection<Object[]> objList = new ArrayList<Object[]>();

		try {
			HSSFWorkbook wb = new HSSFWorkbook(Config.getInstance().getTestFileInputStream());
			PageAction.getHtmlElementMgr();// init the HTML element checking at earlier point

			// load all tests: all test cases are configured in EKPMain page
			
			int dataRowIndex_start = 1;
			int funcType_columnIndex = 0;
			int sheetName_columnIndex = 1;
			int rowNum_columnIndex = 2;
			int importantCase_columnIndex = 4;

			HSSFSheet sheet = wb.getSheet(sheetName_main);

			ArrayList<String> funcTypes = POIUtils.getColumnFromExcel(sheet,
					funcType_columnIndex, dataRowIndex_start);

			ArrayList<String> sheetNames = POIUtils
					.getColumnFromExcel(sheet, sheetName_columnIndex,
							dataRowIndex_start, funcTypes.size());

			ArrayList<String> rowNums = POIUtils.getColumnFromExcel(sheet,
					rowNum_columnIndex, dataRowIndex_start, funcTypes.size());


			ArrayList<String> importantCases = POIUtils.getColumnFromExcel(sheet,
					importantCase_columnIndex, dataRowIndex_start, funcTypes.size());
			
			for (int j = 0; j < funcTypes.size(); j++) {
				String funcType = funcTypes.get(j);
				String sheetName = sheetNames.get(j);
				String rowNum = "";
				String importantCase = importantCases.get(j);
								

				if (rowNums != null && rowNums.size() > j) {
					rowNum = rowNums.get(j);
				}

				sheet = wb.getSheet(sheetName);
				if (sheet != null) {
					int row = 1;

					if (rowNum.equals("")) {
						// Iterate through each rows one by one if not define
						// line number, skip row 0 as it is field names
						boolean found=false;
						for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
							row = i;
							TestObject obj = POIUtils.loadTestCaseFromExcelRow(
									sheetName, funcType, row, wb);
							if (obj != null & !objList.contains(obj)) { // avoid
																		// duplicate
																		// test
																		// cases
								objList.add(new Object[] { obj, obj.toString() });
								addTestObject(obj.getID(), obj);
								found=true;
							}
							if (obj != null) {
								//obj.setLabel(label);
								obj.setImportantCase(importantCase);
							}
						}
						if (!found) {
							System.err.println("<b>TestDriver: data()-1: Cannot find method:"+ funcType + " in sheet:" + sheetName);
							someTestCaseIsMissing=true;
							throw new RuntimeException("ERROR Cannot find method:"+ funcType + " in sheet:" + sheetName);
						}
					} else {
						// only iterate rows specified by rowLine
						String[] rowNum_array = rowNum.split(";");
						for (String rowNum_str : rowNum_array) {
							row = Integer.parseInt(rowNum_str);
							TestObject obj = POIUtils.loadTestCaseFromExcelRow(
									sheetName, funcType, row, wb);
							
							if (obj != null & !objList.contains(obj)) { // avoid
																		// duplicate
																		// test
																		// cases
								objList.add(new Object[] { obj, obj.toString() });
								addTestObject(obj.getID(), obj);
							}
							if (obj == null) {
								System.err.println("<b>TestDriver: data()-2:Cannot find method:"
										+ funcType + " in sheet:" + sheetName
										+ " row:" + rowNum);
								someTestCaseIsMissing=true;
								throw new RuntimeException("ERROR Cannot find method:" + funcType + " in sheet:" + sheetName + " row:" + rowNum);
								
							} else {
								//obj.setLabel(label);
								obj.setImportantCase(importantCase);
							}
						}
					}
				} else {
					//if (Config.DEBUG_MODE) {
					someTestCaseIsMissing=true;
					throw new RuntimeException("<b>Cannot find csv file:" + sheetName);
					//}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		totalTestSuite = objList.size();
		
		if (someTestCaseIsMissing){
			System.err.println("some test cases are missing, pls check the log message");
			return null;
		}else
			return objList;
	}
	@NetdTestRule
	public TimeLogger logger = new TimeLogger(driver);
	
	@Rule
	public TestReport testReport = new TestReport();
	
	@Rule
	public ScreenShotOnFailed screenShootRule = new ScreenShotOnFailed();
	
	
	public static synchronized void switchUser(TestObject testObject, User user){
		
		if (testObject.getTestDriver().currentUser.getUID().equalsIgnoreCase(user.getUID()))
			return;
		// temporary modify the uid of testObject to switch user
		String currentUID= testObject.getUID();
		String currentPWD= testObject.getPWD();
		testObject.setUID(user.getUID());
		testObject.setPWD(user.getPWD());
		switchUser(testObject);
		//set back to orginal user id
		testObject.setUID(currentUID);
		testObject.setPWD(currentPWD);
		testObject.getTestDriver().currentUser=user;
	}
	
	private static synchronized void switchUser(TestObject testObject) {
	
		try {
			String testObject_UID = testObject.getUID();
			if (testObject_UID.equals("")) {// not setup -> defined in super class. 
				String UID = "UID";
				String PWD = "PWD";
				//Search it in super class.ie: Online>LearningModule>TestObject
				Field UID_field = ReflectionUtils.getField_superClz(
						testObject.getClass(), UID);
				Field PWD_Feild = ReflectionUtils.getField_superClz(
						testObject.getClass(), PWD);
				if (UID_field != null) {
					UID_field.setAccessible(true);
					testObject_UID = (String) UID_field.get(testObject);
					User user = new User( (String) UID_field.get(testObject),(String) PWD_Feild.get(testObject));
					testObject.getTestDriver().currentUser=user;
					
				} else {
					//set site.admin as default user
					User user = new User(Config.getInstance().getProperty("user.admin"),Config.getInstance().getProperty("user.admin.pass"));
					testObject.getTestDriver().currentUser=user;
				}
			}
			
			User user = new User(testObject.getUID(), testObject.getPWD());
			user.logout(testObject.getTestDriver().driver);
			user.login(testObject.getTestDriver().driver);
		

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// static HashMap<String, User> id_dbUsers = new HashMap<>();
	//test case can't run more than 3000 seconds, else failed
	@Test(timeout=4000000)
	public void test() throws Exception {
		double startTime, endtime, totaltime;
		startTime = System.currentTimeMillis();
		// 1. print test case info (author, test case no.)
		//String testName = testObject.toString();
		
		System.out.print("Starting: (" + testSuiteNo + "/" + totalTestSuite + ")\t" + testObject.toString() + "\t");
		System.out.println(DateTimeUtils.getCurrentTimeAsStr());
	

		//logger.start(testObject);
		
		try {
			// 3. execute the test case
			if (executeTestMethod(testObject, driver)){
				// 4. do tasks after execution, will not reach this code if exception occur
				//logger.succeeded(testObject);
			}
		} catch (IllegalAccessException e) {
			handFailCaseReporting(e, testObject);
		} catch (NoSuchMethodException e) {
			handFailCaseReporting(e, testObject);
		} catch (StaleElementReferenceException e ) {
			handFailCaseReporting(e, testObject);
		} catch (ElementNotVisibleException e ) {
			handFailCaseReporting(e, testObject);
		} catch (WebDriverException e ) {
			handFailCaseReporting(e, testObject);
		} catch (RuntimeException e) {
			handFailCaseReporting(e, testObject);
		} catch (InvocationTargetException e) {
			handFailCaseReporting(e, testObject);
			// TODO Auto-generated catch block		
		}
		finally{
			//6 do finish task
			//logger.finished(testObject);
			endtime = System.currentTimeMillis();
			totaltime=(endtime-startTime)/1000;
			System.out.println("Finished (" + testSuiteNo + "/" + totalTestSuite + ") "+ testObject.toString() +" in "+ totaltime +" secs " );
			testSuiteNo++;
		}
	}
	
	private void handFailCaseReporting(Exception e,  TestObject obj){
		numFailCases++;
		
		if (testObject.getImportantCase().equals("Y")){
			if (!testObject.getImportantCase_Suite().equals("")){
				actualImportantFail.put(testObject.getID(), testObject.getImportantCase_Suite());
				System.out.println("Test Suite sub case failed  case="+ testObject.getID() +" test suite="+ testObject.getImportantCase_Suite());
			}else{
				actualImportantFail.put(testObject.getID(), testObject.toString());
				System.out.println("Test Suite case failed  case="+ testObject.getID() +" test suite="+ testObject.toString());
			}
		}	
				
		POIUtils.filterDebugMsg(e, testObject);
		//logger.failed(e, obj);
		org.junit.Assert.fail(Throwables.getRootCause(e).toString());
	}
	
	
	
	/**
	 * Execute method(Test Case) specified by funcType based on the following rule: 
	 * A. if field "TestSuite" != null, then ignore this method because it is a test suite and will execute test cases in order. 
	 * B. if field "objectParam" != null, then pass objectParam field value as one param to invoke method.
	 * C. if field "TestSuite"==null and "objectParam" == null, then WebDriver.class is the only param to invoke the method. 
	 * 
	 * @param testObject
	 *            : TestObject-typed instance
	 * @param driver
	 *            : Web Driver   
	 * @return boolean test execute result
	 */
	private boolean executeTestMethod(TestObject testObject, WebDriver driver) throws Exception{
		boolean success = false;
		double startTime, endtime;
		User newUser = new User(testObject.getUID(), testObject.getPWD());
		if (testObject.getFuncType().length()>0) {
			
			testObject.setTestDriver(this);
			this.testReport.setTestObject(testObject);
			this.testReport.setWebDriver(driver);
			this.screenShootRule.setTestObject(testObject);
			this.screenShootRule.setWebDriver(driver);
			
			// 1. Switch user if new test case use different logon user
			TestDriver.switchUser(testObject, newUser);

			Method method = null;
			// 2.1 Execute method directly if no test suites
			
			if (testObject.getTestSuite().trim().length()==0) {
				if (testObject.getObjectParams().size() == 0) {
					// 3.1 if no object param, WebDriver is the only param
					totalExecution++;
					this.testObject = testObject;
					testObject.initilizeElements(driver);
					method = testObject.getClass().getMethod(testObject.getFuncType(),WebDriver.class);
					method.invoke(testObject, driver);
					testReport.SaveSuccessTestReportToExcel();
					success=true;
					

				} else {
					// 3.2 if has object params, WebDriver and ObjectInput
					// are the params
					StringBuilder sb = new StringBuilder();
					System.out.println("\tStarting object inputs in "+testObject.getFuncType());
					for (TestObject objectParam : testObject.getObjectParams()) {
						sb.append(System.lineSeparator()+ "\t\"").append(objectParam.toString()).append("\" ");
					}
					totalExecution++;
					this.testObject = testObject;
					testObject.initilizeElements(driver);
					method = testObject.getClass().getMethod(testObject.getFuncType(),
							WebDriver.class, ArrayList.class);
					startTime = System.currentTimeMillis();
					method.invoke(testObject, driver, testObject.getObjectParams());
					endtime = System.currentTimeMillis();
					testReport.SaveSuccessTestReportToExcel();
					System.out.println("\tFinished object input: "+sb.toString()+(endtime-startTime)/1000+ " secs");
					success=true;
				}
				
			} else {
				// 2.2 If "TestSuite" field is not empty, ignore test suite
				// but execute its test cases
				
				ArrayList<TestObject> testCases = testObject.getTestCaseArray();
				if (testCases!=null && !testCases.isEmpty()) {
					//System.out.println("Starting test suite:" + testObject.toString());

					for (TestObject testCase : testCases) {
						if (testCase != null) {
							// stored original ID
							String ID = testCase.getID();
							testObject.setImportantCase_Suite(testCase.getID());
							// IMPORTANT: modify id for reporting purpose
							// only
							testCase.setID(testObject.getID() + "{"+ testCase.getID() + "}");
							testCase.setImportantCase(testObject.getImportantCase());
							startTime = System.currentTimeMillis();
							boolean testResult=executeTestMethod(testCase, driver);
							endtime = System.currentTimeMillis();
							System.out.println("\tFinished sub case: " + testCase.toString()+" in "+(endtime-startTime)/1000+" secs");
							// reset back to original ID'
							testCase.setID(ID);
							if (!testResult){ 
								System.err.println("ERROR: one test case fail, then skip all coming test cases in test suite");
								break;
							}
							
						}
					}
					//System.out.println("Finshed Test suite: \"" + testObject.toString()+"\"");
					this.testObject = testObject;
					success=true;
				}else{
					System.err.println("<b>ERROR: Loading test cases: \n"+testObject.getTestSuite()+" in test suite: "+testObject.getFuncType()+"</b>");
					throw new RuntimeException("ERROR: Loading test cases: \n"+testObject.getTestSuite()+" in test suite: "+testObject.getFuncType());
				}
			}
		} else {
				System.err.println("methodName:" + testObject.getFuncType()
					+ "() is not defined in class:" + testObject.getClass().getName());
		}
		return success;
	}

	public static int getTotalExecution() {
		return totalExecution;
	}

	public static void setTotalExecution(int totalExecution) {
		TestDriver.totalExecution = totalExecution;
	}

	@After
	public void after() {
		
	}

	@AfterClass
	public static void tearDown() throws Exception {
		// close the window that uses plugin container before driver.quit();
		System.out.println("getting into tearDown now");
		Runtime.getRuntime().exec("taskkill /F /IM plugin-container.exe");
		System.out.println("Total test case run:" + getTotalExecution());
		System.out.println("Total test case fail:" + numFailCases);
		System.out.println("Total test suite run:" + totalTestSuite);
		
		//In case the running do not finish correctly, set the failing cases same as TotalExecution case to block the build copy process
		if (getTotalExecution()<=totalTestSuite)
			numFailCases=getTotalExecution();
		
		String actualImportantFailMsg = "";
		System.out.println("Failed Important Cases:");
		for(Map.Entry<String, String> entry : actualImportantFail.entrySet()){ 
			actualImportantFailMsg += entry.getValue() + "; "; 
			System.out.println(entry.getValue());
		}
		
		
		
		
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put("Total.Cases", String.valueOf(getTotalExecution()));
		properties.put("Total.Pass.Cases", String.valueOf(getTotalExecution()-numFailCases));
		properties.put("Total.Fail.Cases",  String.valueOf(numFailCases));
		properties.put("Failed.Important.Cases", actualImportantFailMsg);
		System.out.println("saving result to "+Config.getInstance().getProperty("test.result"));
		PropertiesFileUtils.SaveAsPropertiesFile(Config.getInstance().getProperty("test.result"), properties);		
	}
	
//	public static Object getCurrentTestObject() {
//		// TODO Auto-generated method stub
//		return "Not yet supported";
//	}
}

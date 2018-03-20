package com.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.internal.ElementScrollBehavior;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import com.config.Config;
import com.page.navigate.PageInteraction;
import com.page.test.User;
import com.page.utils.InteractionUtils;

public class TestDriverUtils {
	

	/** 
	 * For WebDriver creating and webdriver List maintenance 
	 * 
	 * @author Lester  Li
	 */

	public enum Type {
		FireFox,  Chrome, IE,Edge,Safari }

	private static Stack<WebDriver> webDriverList = new Stack<WebDriver>();

	 
	
	// Call this method to get a usable webdriver from stack. If stack is empty, it will create one
	public static synchronized WebDriver acquireWebDriver() throws IOException {
	
		WebDriver driver;
		if(webDriverList.empty()) {
			System.out.println("Creating new webdriver...");
			driver = getWebDriver_new();
			User loginUser = new User(Config.getInstance().getProperty("site.admin", driver), Config.getInstance().getProperty("site.admin.pass", driver));
			loginUser.login(driver);
		} else {
			driver = webDriverList.pop();
		}
	
		return driver;
	}
	
	// Always call this method after finish using a webdriver
	public static synchronized void returnWebDriver(WebDriver driver) {
	
		webDriverList.push(driver);

	}


	public static WebDriver getWebDriver_new() throws IOException {
		WebDriver driver;

		if (Config.getInstance().getProperty("TragetRunner").equalsIgnoreCase(Type.FireFox.toString())){
			 driver = getWebDriver_new(TestDriverUtils.Type.FireFox);
		}else if (Config.getInstance().getProperty("TragetRunner").equalsIgnoreCase(Type.IE.toString())){
			 driver = getWebDriver_new(TestDriverUtils.Type.IE);
		}else if (Config.getInstance().getProperty("TragetRunner").equalsIgnoreCase(Type.Edge.toString())){
				 driver = getWebDriver_new(TestDriverUtils.Type.Edge);
		}else {
			 driver = getWebDriver_new(TestDriverUtils.Type.Chrome);
		}
		
		
		PageInteraction.utils.getVisitedWinsMap().put(driver, new ArrayList<String>());
		return driver;
	}

	public static WebDriver getWebDriver_new(Type browser_type) {
		
		WebDriver driver = null;
		
		LoggingPreferences pref = new LoggingPreferences();
	    pref.enable(LogType.BROWSER, Level.OFF);
	    pref.enable(LogType.CLIENT, Level.OFF);
	    pref.enable(LogType.DRIVER, Level.OFF);
	    pref.enable(LogType.PERFORMANCE, Level.OFF);
	    pref.enable(LogType.PROFILER, Level.OFF);
	    pref.enable(LogType.SERVER, Level.OFF);

		switch (browser_type) {
		case FireFox:
			System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")
					+File.separator +"lib"+File.separator+"geckodriver.exe");
			DesiredCapabilities dc = null;
			FirefoxProfile firefoxProfile = new FirefoxProfile();
		  	firefoxProfile.setPreference("browser.helperApps.alwaysAsk.force",
					false);
		  	firefoxProfile.setPreference(
					"browser.download.manager.showWhenStarting", false);
		  	//firefoxProfile.setPreference("font.size.variable.x-western",10);
		  	firefoxProfile.setPreference("browser.download.folderList", 2);
		  	firefoxProfile.setPreference("browser.download.dir",
		  			System.getProperty("user.dir")
							+ "\\"
							+ Config.getInstance().getProperty("download.dir", driver)
									.toString());
		  	firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
							"application/x-xliff+xml,application/xliff+xml, application/octet-stream");
			
		  	dc = DesiredCapabilities.firefox();
		  	dc.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
		  	dc.setCapability("acceptSslCerts", true);
		  	dc.setCapability("unexpectedAlertBehaviour", "accept"); // "dismiss","ignore"
		  	dc.setCapability("databaseEnabled", true);
		  	dc.setCapability("javascriptEnabled", true);
		  	dc.setCapability("elementScrollBehavior", 0); // 0- from Top, 1 - from bottom
		    dc.setCapability(CapabilityType.LOGGING_PREFS, pref);

			
			//FirefoxBinary binary = new FirefoxBinary(new File(Config.getInstance().getProperty("FirefoxPath")));
			//binary.setEnvironmentProperty("DISPLAY",System.getProperty("lmportal.xvfb.id",":99"));
			//driver = new FirefoxDriver(binary,firefoxProfile);
		  	

			driver = new FirefoxDriver(dc);
			break;
			
		case Chrome:	
			//Among the facilities provided by the System class are standard input, standard output, and error output streams; access to externally defined "properties"; a means of loading files and libraries; and a utility method for quickly copying a portion of an array.
			 System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")
					 +File.separator +"lib"+File.separator+"chromedriver.exe");
			 ChromeOptions options = new ChromeOptions();
		        options.addArguments("--test-type");
		        options.addArguments("--disable-extensions"); 
		        options.addArguments("--start-maximized");
		        options.addArguments("--disable-infobars");
			 driver = new ChromeDriver(options);
			break;
			
		case IE: 
			System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")
					+File.separator +"lib"+File.separator+"IEDriverServer.exe");
			
			InternetExplorerOptions ieOptions = new InternetExplorerOptions();
			ieOptions.enablePersistentHovering();
			ieOptions.introduceFlakinessByIgnoringSecurityDomains();
			// This functionality is currently considered extremely experimental; use at your own risk.
			//ieOptions.enableNativeEvents();
			//ieOptions.requireWindowFocus();
			ieOptions.elementScrollTo(ElementScrollBehavior.TOP);
			ieOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
			ieOptions.ignoreZoomSettings();
			ieOptions.withInitialBrowserUrl(Config.getInstance().getProperty("loginURL"));
			ieOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
			//ieOptions.destructivelyEnsureCleanSession();
			ieOptions.setCapability("acceptSslCerts", true);
			ieOptions.setCapability("javascriptEnabled", true);
			ieOptions.setCapability("databaseEnabled", true);
			ieOptions.setCapability("unexpectedAlertBehaviour", "accept"); // "dismiss","ignore"
			//ieOptions.setCapability("nativeEvents", true);
            driver = new InternetExplorerDriver(ieOptions);
			
		 //NOT WORKING	
			//Among the facilities provided by the System class are standard input, standard output, and error output streams; access to externally defined "properties"; a means of loading files and libraries; and a utility method for quickly copying a portion of an array.
			
			
			/*dc = DesiredCapabilities.internetExplorer();
				dc.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, Config.getInstance().getProperty("loginURL"));
				//dc.setCapability(InternetExplorerDriver.LOG_LEVEL, "DEBUG");
				dc.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				dc.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, true);
				dc.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
			    dc.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
			 
				dc.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,true);
				dc.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
				dc.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true); 
				dc.setJavascriptEnabled(true); 
				dc.setCapability("disable-popup-blocking", true);
				//dc.setCapability(InternetExplorerDriver.LOG_LEVEL, "DEBUG");
				//dc.setCapability("nativeEvents",false);
				dc.setCapability("acceptSslCerts", true);
				dc.setCapability("unexpectedAlertBehaviour", "accept"); // "dismiss","ignore"
				dc.setCapability("databaseEnabled", true);
				//dc.setCapability("javascriptEnabled", true);
				dc.setCapability("elementScrollBehavior", 0); // 0- from Top, 1 - from bottom
			 	*/

			//driver = new InternetExplorerDriver(dc);
			break;
			
		case Edge: // lastest support version of edge is 16 which is now not available in anywhere
			System.setProperty("webdriver.edge.driver", 
					System.getProperty("user.dir")+File.separator +"lib"+File.separator+"MicrosoftWebDriver.exe");
			EdgeOptions edgeoptions = new EdgeOptions();
			edgeoptions.setPageLoadStrategy("eager");
			driver = new EdgeDriver(edgeoptions);
		case Safari:
			System.setProperty("webdriver.safari.driver", 
					Config.getInstance().getProperty("Mac.WebDriver.Path")+"safaridriver.exe");
			driver = new SafariDriver();
		}
		
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(200, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(2500, TimeUnit.SECONDS);
		driver.manage()
				.timeouts()
				.implicitlyWait(
						Integer.parseInt(Config.getInstance().getProperty(
								"ImplicitWait_millis", driver)), TimeUnit.MILLISECONDS);
		return driver;
	}


}

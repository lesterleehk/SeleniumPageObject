package com.config;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.WebDriver;

import com.java.utils.MapFormatUtils;
import com.java.utils.Validate;



/**
 * @author lester.li
 * This is config. class which is used to load all config. item with key value pair 
 * This is also a singleton class which has one instance only in whole system
 */
public class Config {

	public static boolean DEBUG_MODE=false;
	
	public static boolean enableHighlighter = false;
	
	public static boolean PRINTELEMENTNOTFOUNDMSG = false;
	
	public static String DELIMIT = "|";
	
	
	public static final String NTS_Domain_DB = "nts_domain_db";
	public static final String Baby_Fresh_DB = "baby_fresh_db";
	public static final String NTS_UMA_DB = "nts_uma_db";

	private static Properties testingProperties;
	
	private  Properties ekpProperties;
	
	private  Properties targetDBProperties;
	
	//private  Properties standard_en_properties;
	
	private  Properties allProperties;
	
	private static final Config instance = new Config();
	
	private final Locale defaultLocale = new Locale("en");
	//private Locale userLocale;
	
	//private ResourceBundleImpl userLocaleBundle;

	public static ConcurrentHashMap<WebDriver, ResourceBundleImpl> userLocaleBundleMap = new ConcurrentHashMap<WebDriver, ResourceBundleImpl>();

	public void setUserLocale(Locale newLocale, WebDriver driver) {
		Locale userLocale = newLocale;
		//userLocaleBundle = new ResourceBundleImpl(this.userLocale);
		userLocaleBundleMap.put(driver, new ResourceBundleImpl(userLocale));
	}

	public static Config getInstance() {
	    return instance;
    }

	public String getProperty(String key){
		String value=allProperties.getProperty(key);
		if (Validate.isBlank(value)){
			return "Cannot find the property value with key="+key;
		}else{
			return value;
		}
	}
	
	public String getProperty(String key, WebDriver driver) {
		String value = getProperty(key);
		if(value.equals("Cannot find the property value with key="+key)){
			value = userLocaleBundleMap.get(driver).getString(key);
			if (Validate.isBlank(value)){
				return new ResourceBundleImpl(defaultLocale).getString(key);
			}
		}
		return value.trim();
	}
	public FileInputStream getTestFileInputStream(){
		String targetDB=allProperties.getProperty("TargetDB");
		try {
			switch (targetDB.toLowerCase()){
				case Config.NTS_Domain_DB:
					return new FileInputStream(Config.getInstance()
							.getProperty(targetDB));
				case NTS_UMA_DB:
					return new FileInputStream(Config.getInstance()
							.getProperty(targetDB));
				case Baby_Fresh_DB:
					return new FileInputStream(Config.getInstance()
							.getProperty(targetDB));
				default:
					throw new RuntimeException("ERROR cannot find the target DB");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public  void setProperty(String key, String value){
		allProperties.setProperty(key, value);
	}
	
	private static void loadProperties(Properties prop, String sProperties)
	{
		
		InputStream input = null;
		try {
			input = new FileInputStream(sProperties);
			// load a properties file
			prop.load(input);
			
			// only use replacing in Selenium conf.
			if (!(prop.getProperty("test.report.dir")==null)){
				Map<String, String> map = new HashMap<String, String>();
				map.put("IP", prop.getProperty("IP"));
				map.put("port",  prop.getProperty("port"));
				map.put("domain",  prop.getProperty("domain"));
				map.put("configDir",  prop.getProperty("configDir"));
				map.put("resourceDir",  prop.getProperty("resourceDir"));
				map.put("test.report.dir",  prop.getProperty("test.report.dir"));
				map.put("screenShotDir", prop.getProperty("screenShotDir"));
				map.put("skikuliDir", prop.getProperty("skikuliDir"));
				map.put("tomcatDir", System.getenv("CATALINA_HOME") +"/webapps");
				map.put("ImplicitWait_millis", prop.getProperty("ImplicitWait_millis"));
				map.put("ExplicitWait_millis", prop.getProperty("ExplicitWait_millis"));
				map.put("HighlightElement_millis", prop.getProperty("HighlightElement_millis"));
				map.put("dateFormat", prop.getProperty("dateFormat"));
				prop.setProperty("baseURL", MapFormatUtils.format(prop.getProperty("baseURL"), map));
				map.put("baseURL",prop.getProperty("baseURL"));
				
				//replace the {baseURL} with = prop.getProperty("baseURL") in loginURL property
				 for (Iterator iter = prop.keySet().iterator(); iter.hasNext();) {
					 String key = (String) iter.next();
					 prop.setProperty(key, MapFormatUtils.format(prop.getProperty(key), map)); 
				 }
				if (Boolean.getBoolean(prop.getProperty("enableHighlighter"))){
					enableHighlighter=true;
				}
				if (Boolean.getBoolean(prop.getProperty("DEBUG_MODE"))){
					DEBUG_MODE=true;
				}
			}
				
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Config(){
		
		testingProperties =new Properties();
		targetDBProperties=new Properties();
		//standard_en_properties = new Properties();
		allProperties = new Properties();
		loadProperties(testingProperties, "./conf/config.properties");
		allProperties.putAll(testingProperties);
		loadProperties(targetDBProperties, "./conf/TargetDB.properties");
		allProperties.putAll(targetDBProperties);

		
		
		//setUserLocale(defaultLocale);
		System.out.println("-------------------NTS Config Detail--------------------------");
		System.out.println("baseURL: " + getProperty("baseURL"));
		System.out.println("loginURL: " + getProperty("loginURL"));
		System.out.println("Default UID: " + getProperty("user.admin"));
		System.out.println("Default PWD: " + getProperty("user.admin.pass"));	
		System.out.println("default.user: " + getProperty("default.user"));
		System.out.println("-----------------------------------------------------------------");
		

	}

		
}

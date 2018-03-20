package com.junit;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
 
//import org.openqa.selenium.*;
 
//import org.openqa.selenium.firefox.*;
 
public class CheckLinks {
	private static WebDriver driver = null;
 
  public static List findAllLinks(WebDriver driver)
 
  {
 
	  List<WebElement> elementList = new ArrayList<WebElement>();
 
	  elementList = driver.findElements(By.tagName("a"));
 
	  elementList.addAll(driver.findElements(By.tagName("img")));
 
	  List finalList = new ArrayList(); ;
 
	  for (WebElement element : elementList)
 
	  {
 
		  if(element.getAttribute("href") != null)
 
		  {
 
			  String href=element.getAttribute("href");
			  if (!href.contains("javascript")) {
				  finalList.add(element);
			  }
 
		  }		  
 
	  }	
 
	  return finalList;
 
  }
 
	public static String isLinkBroken(URL url) throws Exception
 
	{
 
		//url = new URL("http://yahoo.com");
 
		String response = "";
 
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
 
		try
 
		{
 
		    connection.connect();
 
		     response = connection.getResponseMessage();	        
 
		    connection.disconnect();
 
		    return response;
 
		}
 
		catch(Exception exp)
 
		{
 
			return exp.getMessage();
 
		}  				
 
	}
	
	public  static boolean clickjavascipt(WebElement elemt) {
		String url =driver.getCurrentUrl();
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click()", elemt);
		
		driver.navigate().to(url);
		return true;
		
	}
 
	public static void main(String[] args) throws Exception {
		
 
		// TODO Auto-generated method stub
 
			 System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")
					 +File.separator +"lib"+File.separator+"chromedriver.exe");
			 ChromeOptions options = new ChromeOptions();
		        options.addArguments("--test-type");
		        options.addArguments("--disable-extensions"); 
		        options.addArguments("--start-maximized");
		        options.addArguments("--disable-infobars");
		        
		        
			 driver = new ChromeDriver(options);

			 driver.get("http://beta.baby-kingdom.com");
			 //driver.get("http://www.baby-kingdom.com");
			 PrintWriter out = new PrintWriter(new FileWriter(System.getProperty("user.dir")+File.separator+"outputfile.txt")); 
		    List<WebElement> allImages = findAllLinks(driver);    
 
		    System.out.println("Total number of elements found " + allImages.size());
		    int count=1;
		    for( WebElement element : allImages){
 
		    	try
 
		    	{
		    		String returnResult, href;
		    		href=element.getAttribute("href");
//		    		if (href.contains("javascript")) {
//		    			if (clickjavascipt(element))
//		    				returnResult="ok";
//		    			else
//		    				returnResult="javascript failed";
//		    		}else {
//		    		 returnResult= isLinkBroken(new URL(href));
//		    		}
		    		returnResult= isLinkBroken(new URL(href));
		    				
		    		if (returnResult.equalsIgnoreCase("ok")|| returnResult.contains("Moved")) {
		    			// do nothing
		    		}else {
		    			 out.println(count+": URL: " + element.getAttribute("href")+ " returned "+ isLinkBroken(new URL(element.getAttribute("href"))));
		    		}
		    		
		    		//System.out.println("URL: " + element.getAttribute("outerhtml")+ " returned " + isLinkBroken(new URL(element.getAttribute("href"))));
 
		    	}
 
		    	catch(Exception exp)
 
		    	{
 
		    		System.out.println(count+": At " + element.getAttribute("innerHTML") + " Exception occured -&gt; " + exp.getMessage());	    		
 
		    	}
		    	
		    	count++;
 
		    }

			 out.close();
	    }
 
	}
package com.java.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.config.Config;

/**
 * use to save properties as text file
 * @author lester.li
 *
 */
public class PropertiesFileUtils {
	
	// Suppress default constructor for noninstantiability
	private PropertiesFileUtils() {

		throw new AssertionError();
	}
	

    public static boolean SaveAsPropertiesFile(String fullFileName, Map<String, String> table){
    	Properties prop = new Properties();
    	OutputStream output = null;
    	boolean saveSucccess=false;
    	try {
     
    		output = new FileOutputStream(fullFileName, false);
    		// set the properties value
    		 for (Iterator<String> iter = table.keySet().iterator(); iter.hasNext();) {
				 String key = iter.next();
				 String value= table.get(key);
				 prop.setProperty(key,value); 
				 System.out.println("saving key="+key+" and value="+value);
			 }
     
    		// save properties to project root folder
    		prop.store(output,null);
     
    	} catch (IOException io) {
    		io.printStackTrace();
    	} finally {
			try {
				output.flush();
				output.close();
			} catch (IOException e) {
				System.out.println("output file failed");
				e.printStackTrace();
			}
    	}
    
    	return saveSucccess;
    }
    
    public static Properties loadProperties(String fullFileName){
    	Properties prop = new Properties();
    	InputStream input = null;
     
    	try {
     
    		input = new FileInputStream(fullFileName);
     
    		// load a properties file
    		prop.load(input);
     
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
    	return prop;
    }
}

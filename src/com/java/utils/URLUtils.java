package com.java.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.config.Config;

public class URLUtils {
	
	public static String URL_Prefix = Config.getInstance().getProperty("baseURL");
	
	public static String decodeURL(String URL){
		String result = "";
		try {
			result = URLDecoder.decode(URL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}

}

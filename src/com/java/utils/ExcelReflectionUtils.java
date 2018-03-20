package com.java.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.config.Config;

/**
 * 
 * @author lester.li
 *
 */
public class ExcelReflectionUtils {
	
	// Suppress default constructor for noninstantiability
	private ExcelReflectionUtils() {

		throw new AssertionError();
	}
	
	public static String LINESEPARATOR="\n";
	public static String FILE_PATH_SEPARATOR="/";
	
	
	/**2014-06-04:override abstract methods
	 * 
	 * @param superClz
	 * @return
	 */
	private static String handleAbstractMethods(String superClz){
		StringBuilder sb = new StringBuilder();
		
		try {
		
			ArrayList<Method> abstractMethods = new ArrayList<Method>();
			Class clz = Class.forName(superClz);
			Method[] methods = clz.getMethods();
			for(Method method: methods){
				if(Modifier.isAbstract(method.getModifiers())){
					abstractMethods.add(method);	
				}
			}
			
			for(Method method: abstractMethods){
				/*sb.append(method.toString()).append("{").
				append(LINESEPARATOR).append("}").append(LINESEPARATOR).append(LINESEPARATOR);*/
				
				sb.append(translateReflectedMethod(method));
			}
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private static String translateReflectedMethod(Method method){
		StringBuilder sb = new StringBuilder();
		String modifier = Modifier.toString(method.getModifiers() - Modifier.ABSTRACT);
		String returnType = method.getReturnType().toString();
		String methodName = method.getName();
		Class[] paraList = method.getParameterTypes();
		
		sb.append(modifier).append(" ").append(returnType).
		append(" ").append(methodName).
		append("(");
		
		int i = 0;
		for(; i < paraList.length - 1; i ++){
			Class para = paraList[i];
			sb.append(para.getName()).append(" para"+i+",");
		}
		
		Class para = paraList[i];
		sb.append(para.getName()).append(" para"+i+"){").append(LINESEPARATOR);
		
		//method body
		if(!returnType.equalsIgnoreCase("void")){
			Class type = method.getReturnType();
			sb.append(method.getReturnType().toString()).append(" result = ").
			append(getDefaultValue(type)).append(";").append(LINESEPARATOR);
			
			sb.append("return result;");
		} 
		
		sb.append("}").append(LINESEPARATOR).append(LINESEPARATOR);
		
		return sb.toString();
	}
	
	
	public static Object getDefaultValue(Class clazz) {
		Object result = null;
        if (clazz.equals(boolean.class)) {
            result = false;
        } else if (clazz.equals(byte.class)) {
            result =  0;
        } else if (clazz.equals(short.class)) {
            result = 0;
        } else if (clazz.equals(int.class)) {
            result =  0;
        } else if (clazz.equals(long.class)) {
            result =  0;
        } else if (clazz.equals(float.class)) {
            result = 0.0f;
        } else if (clazz.equals(double.class)) {
            result = 0.0d;
        } else{
        	result = null;
        }
        
        return result;
    }

}

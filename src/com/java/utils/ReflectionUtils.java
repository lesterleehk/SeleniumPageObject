package com.java.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.abstractclasses.TestObject;

/**
 * 
 * @author lester.li
 *
 */
public class ReflectionUtils {
	
	// Suppress default constructor for noninstantiability
	private ReflectionUtils() {

		throw new AssertionError();
	}
	
	public static Field getField_superClz(Class<? extends TestObject> clz, String fieldName){
			
		//System.out.println("Clz.getName()="+clz.getName()+"; fieldName="+fieldName);
		Field field = null;
		Class<? extends TestObject> orginalClz=clz;
		try {
			Class superClz = null ;
			boolean isFieldExist = false;
			do{
				superClz = clz.getSuperclass();
				//System.out.println("superClz.getName()="+superClz.getName()+"; fieldName="+fieldName);
				if(superClz != null){
					isFieldExist = ReflectionUtils.isFieldExist(superClz, fieldName);
					clz = superClz;	
				}
			}while(!isFieldExist && superClz != null);
			
			if(isFieldExist){
				field = superClz.getDeclaredField(fieldName);	
			}else {
				System.out.println("In sub class: "+orginalClz.getName()+" cls,cannot find field:" + fieldName);
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return field;
	}
	
	
	public static boolean isFieldExist(Class clz, String fieldName){
		Field[] fields=clz.getDeclaredFields();
		boolean contain = false;
		for(Field field: fields){
			//System.out.println(field.getName()+"="+fieldName);
			if(field.getName().equals(fieldName)){
				contain = true;
				break;
			}
		}
		
		return contain;
	}

	
	public static Field[] getFields(String clzName){
		Class clz = ReflectionUtils.loadClass(clzName);
		return clz.getDeclaredFields();
	}
	
	public static Class loadClass(String clzName){
		Class clz = null;
		
		try {
			clz = Class.forName(clzName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return clz;
	}
	
	public static Object loadObject(String clzName){
		Object obj = null;
		
		try {
			Class clz = Class.forName(clzName);
			obj = clz.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public static Field getField(Class clz, String fieldName){
		Field field = null;
		
		try {
			if(ReflectionUtils.isFieldExist(clz, fieldName)){
				field = clz.getDeclaredField(fieldName); 
			}else{
				//search field in super classes
				field = ReflectionUtils.getField_superClz(clz, fieldName);
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return field;
	}
	
	public static String getFieldValueAsString(TestObject testObject, String fieldName){
		String fieldValue = "";
		
		try {
			Class clz = testObject.getClass();
			Field field = ReflectionUtils.getField(clz, fieldName);
			field.setAccessible(true);
			fieldValue = (String) field.get(testObject);
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
		return fieldValue;
	}
	
	
	public static ArrayList<TestObject> getFieldValueAsTestObjectArray(TestObject testObject, String fieldName){
		ArrayList<TestObject> fieldValue = null;
		
		try {
			Class clz = testObject.getClass();
			Field field = ReflectionUtils.getField(clz, fieldName);
			field.setAccessible(true);
			fieldValue = (ArrayList<TestObject>) field.get(testObject);
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
		return fieldValue;
	}
	
	
}


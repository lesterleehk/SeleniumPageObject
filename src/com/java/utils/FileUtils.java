package com.java.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileUtils {

	/**
	 * Used to create a file in a provided path.
	 * the parameter should contain a completed path and file name, 
	 * for example: C:\testfolder\testfile.txt
	 * 
	 * @param destFileName
	 * @return
	 */
	public static boolean createFile(String destFileName) {
		 File file = new File(destFileName);  
	        if(file.exists()) {  
	            System.out.println("create single file " + destFileName + " failed, it has existed!");  
	            return false;
	        }  
	        if (destFileName.endsWith(File.separator)) {  
	            System.out.println("create single file " + destFileName + " failed, the path need include the name of single file!");  
	            return false;
	        }  
	        //the path of destfile exist or not
	        if(!file.getParentFile().exists()) {  
	            System.out.println("the path of destfile doesn't exist, ready to create it!");  
	          //if not, create it 
	            if(!file.getParentFile().mkdirs()) {  
	                System.out.println("fail to create it");  
	                return false; 
	            }  
	        }  
	        //create the destfile
	        try {  
	            if (file.createNewFile()) {  
	                System.out.println("create single file " + destFileName + " successful!");  
	                return true;  
	            } else {  
	                System.out.println("create single file " + destFileName + " failed!");  
	                return false;  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            System.out.println("create single file " + destFileName + " failed!" + e.getMessage());  
	            return false;  
	        }  
	}

	/**
	 * append or write string to a text file in a provided path.
	 * the parameter should contain a completed path and file name, 
	 * for example: C:\testfolder\testfile.txt
	 * 
	 * @param destFileName
	 */
	public static void WriteStringToFile(String destFileName) {  
        try {  
            FileWriter fw = new FileWriter(destFileName, true);  
            BufferedWriter bw = new BufferedWriter(fw);  
            bw.append("appending text");  
            bw.write("testing text\r\n ");// text  
            bw.write("def\r\n ");  
            bw.write("hijk ");  
            bw.close();  
            fw.close();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
	}
	
	public static void WriteStringToFile(String destFileName, String appendingTextString) {  
        try {  
            FileWriter fw = new FileWriter(destFileName, true);  
            BufferedWriter bw = new BufferedWriter(fw);  
            bw.append("appending text");  
            bw.write(appendingTextString + "\r\n ");// text  
            bw.close();  
            fw.close();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
	}
	
	
}

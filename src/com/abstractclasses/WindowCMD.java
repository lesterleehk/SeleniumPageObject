package com.abstractclasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * @author lester.li
 * This is abstract class which can be extend to implement different Window Command
 */
public abstract class WindowCMD {
	
	protected ArrayList<String> commandScript;
	
	public WindowCMD(){
		this.commandScript= new ArrayList<String>();
		this.commandScript.add("cmd.exe");
		this.commandScript.add("/c");
	}
	
	public void executeCMD() {
		String commmand="";
		String[] commmandArr = new String[commandScript.size()];
		commmandArr = commandScript.toArray(commmandArr);
		for(String s : commmandArr)
			commmand=commmand+s; 
		System.out.println(commmand); 
		
		Process process;  
        try {  
        	process = new ProcessBuilder(commmandArr).redirectErrorStream(true).start(); 
            InputStream is = process.getInputStream();  
            InputStreamReader isr = new InputStreamReader(is);  
            BufferedReader br = new BufferedReader(isr);  
            String line;  
  
            while ((line = br.readLine()) != null) {  
                if (line.matches("controlservice failed")) {  
                    System.out.println(".......");  
                }  
                System.out.println(line);  
            }  
  
            br.close();  
            isr.close();  
            is.close();  
            process.destroy();  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
  
	}
}

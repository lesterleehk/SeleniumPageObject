package com.java.utils;

import java.io.File;

import org.sikuli.basics.ImageLocator;
import org.sikuli.basics.Settings;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

/**
 * 
 * @author lester.li
 *
 */
public class SikuliUtils {
	static{
		String fullPath = new File(System.getProperty("user.dir"), "images.sikuli").getAbsolutePath();
		ImageLocator.setBundlePath(fullPath);
		Settings.MinSimilarity=0.9;
	}
	
	// Suppress default constructor for noninstantiability
	private SikuliUtils() {

		throw new AssertionError();
	}
	
	/**Find an image in a window with Sikuli
	 * 
	 * @param screenshotFile
	 * @return
	 */
	public static boolean screenshotExistInWin(String screenshotFile){

		boolean exist = false;
		try {
			Region win = App.focusedWindow();
			
			int debugLightSecs = 3; // set to 0 to switch off highlighting
			Screen s = new Screen();
			
			Pattern target = new Pattern(screenshotFile);
			target.setTimeAfter(50);
			target.similar(0.85f);
			
			Match m = s.find(target);
			if(m != null){
				m.highlight(debugLightSecs);
				exist = true;
			}
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return exist;
		
//		return true;
	}

}

package com.java.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import com.config.Config;

import au.com.bytecode.opencsv.CSVReader;



public class Convert_CSV_XLS {  

	public enum Commands {
		$Today(Pattern.compile("\\{\\$Today\\s*(\\+|\\-)?\\s*(\\d*)\\}")), //Today + or - Days. ie {$Today+1} or {$Today-1}
		$YearBegin(Pattern.compile("\\{\\$YearBegin\\s*(\\+|\\-|\\s)?\\s*(\\d*)\\}")), //This year + or - ie.  {$YearBegin+1} = this year+1 = 2017+1; Next year begin day= Jan 1, 2018
		$YearEnd(Pattern.compile("\\{\\$YearEnd\\s*(\\+|\\-)?\\s*(\\d*)\\}")); //This year + or - ie.  {$YearEnd+1} = this year+1 = 2017+1; Next year end day= Dec 31, 2018
		private Commands(Pattern regexPattern){
			this.regexPattern = regexPattern;
		}
		private final Pattern regexPattern;
/**
 * the placeholder matching the pattern will be replaced with the corresponding date value
 * @author jasonwc.wong
 * @param String target = target string with some date placeholder, 
 * @param Commands c = the placeholder to be replaced
 * 
 */
		public static String getAllDateTimeReplaced(String target, Commands c){
			String datetime=target;
			Matcher matcher = c.regexPattern.matcher(datetime);
			while (matcher.find())
			{
				int number = 0;
				if(matcher.group(1)!=null && !matcher.group(2).equals("")){
					//$Today + days
					number = Integer.parseInt(matcher.group(1)+matcher.group(2));
				}else if (matcher.group(1)==null && matcher.group(2).equals("")){
					//"{$Today }"
					number = 0;
				}else if (matcher.group(1)==null || matcher.group(2).equals("")){
					//"{$Today +} " or "{$Today1}"
					datetime = matcher.replaceFirst("<Error in translation>");
					matcher = c.regexPattern.matcher(datetime);
					continue;
				}
				//replacement steps
				switch(c){
				case $Today:
					datetime = matcher.replaceFirst(DateTimeUtils.calendarDateToString( Calendar.getInstance(),  number));//e.g. -10, +5, -3...
					break;
				case $YearBegin:
					datetime = matcher.replaceFirst(DateTimeUtils.calendarBeginDateOfYear(Calendar.getInstance(), number)); 
					break;
				case $YearEnd:
					datetime = matcher.replaceFirst(DateTimeUtils.calendarEndDateOfYear(Calendar.getInstance(), number));
					break;
				}
				matcher = c.regexPattern.matcher(datetime);
				
			}
			return datetime;
		}
	}
	
	private static File[] files;
	private static String fileExtension=".csv";
	public static File[] getFileList(){
		File dir = new File(getTargetCsvFilePath());
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(fileExtension);
		    }
		});
		return files;
	}
	
	public static void writeToExcelSheet(String FileName, HSSFWorkbook new_workbook, CSVReader reader){
		String[] nextLine; /* for every line in the file */  
		new_workbook.setForceFormulaRecalculation(true);
		HSSFSheet sheet = new_workbook.createSheet(FileName);  //create a worksheet with caption score_details
		CellStyle cellStyle = new_workbook.createCellStyle();
		FormulaEvaluator evaluator = new_workbook.getCreationHelper().createFormulaEvaluator();
		//HSSFDataFormat df = new_workbook.createDataFormat();
		//cellStyle.setDataFormat(df.getFormat("m/d/yy h:mm"));
		//cellStyle.setDataFormat(new_workbook.getCreationHelper().createDataFormat().getFormat("4"));
		
		   /* Step -3: Define logical Map to consume CSV file data into excel */
		   ArrayList<String[]> excel_data = new ArrayList<String[]>(); //create a map and define data
		   /* Step -4: Populate data into logical Map */
		   try {
				while ((nextLine = reader.readNext()) != null) {
				        excel_data.add(nextLine);
				};
		   	}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   
		   int rownum = 0;
	        for (String[] feilds : excel_data) { //loop through the data and add them to the cell
	                Row row = sheet.createRow(rownum++);
	                int cellnum = 0;
	                for (Object obj : feilds) {
	                	String theString= ((String)(obj)).trim();
		                Cell cell = row.createCell(cellnum++);
		               if (isIncludeDatetime(theString)){
		                    cell.setCellValue(getDatetime(theString));
		               }else if(theString.startsWith("=")){  
		            	   //HSSFDataFormat df = new_workbook.createDataFormat();
		           			//cellStyle.setDataFormat(df.getFormat("m/d/yy"));
		            	   cellStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("d-mmm-yy"));
		            	   cell.setCellType(Cell.CELL_TYPE_FORMULA);
		            	   cell.setCellStyle(cellStyle);
	                		cell.setCellFormula(theString.replace("=", ""));
	                	
	                		evaluator.evaluateFormulaCell(cell);
		            	   
		               }else if(obj instanceof Double){
		                	cell.setCellStyle(cellStyle);
		                	
		                    cell.setCellValue((Double)obj);
		                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		                    //cell.setCellType(Cell.CELL_TYPE_FORMULA);
	                	}else if(isInteger(theString)){
	                		
		                	cell.setCellStyle(cellStyle);
		                	
		                    cell.setCellValue(Integer.valueOf(theString));
		                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		                    //cell.setCellType(Cell.CELL_TYPE_FORMULA);
	                	}else{
	                		//cell.setCellType(Cell.CELL_TYPE_FORMULA);
	                		cell.setCellValue(theString);
	                	}
		                
	                }
	        }
	        HSSFFormulaEvaluator.evaluateAllFormulaCells(new_workbook);
   }
	public static boolean isIncludeDatetime(String theString) {
		return theString.contains(Commands.$Today.toString()) || theString.contains(Commands.$YearBegin.toString()) || theString.contains(Commands.$YearEnd.toString());
	}
	
	public static String getDatetime(String theString) {
		String output = theString;
		output = Commands.getAllDateTimeReplaced(output, Commands.$Today);
		output = Commands.getAllDateTimeReplaced(output, Commands.$YearBegin);
		return Commands.getAllDateTimeReplaced(output, Commands.$YearEnd);
		
		
/*
		String datetime="";
		 if (theString.contains(Commands.$Today.toString())){
             // Default is 0
        	   Integer days= new Integer("0");
            	if (theString.length()>6)
            		if (theString.contains("+")) // "+" operator
            			days=Integer.valueOf(theString.split("\\+")[1].trim());
            		else // "-" operator
            			days=Integer.valueOf("-"+theString.split("\\-")[1].trim());
            	 datetime= DateTimeUtils.calendarDateToString( Calendar.getInstance(), days.intValue() );          
           }else if (theString.contains(Commands.$YearBegin.toString())){ 
        	   Integer years= new Integer("0");
            	if (theString.length()>10)
            		if (theString.contains("+")) // "+" operator
            			years=Integer.valueOf(theString.split("\\+")[1].trim());
            		else // "-" operator
            			years=Integer.valueOf("-"+theString.split("\\-")[1].trim());
            	 datetime= DateTimeUtils.calendarBeginDateOfYear(Calendar.getInstance(), years); 
           
           }else if (theString.contains(Commands.$YearEnd.toString())){ 
        	   Integer years= new Integer("0");
            	if (theString.length()>8)
            		if (theString.contains("+")) // "+" operator
            			years=Integer.valueOf(theString.split("\\+")[1].trim());
            		else // "-" operator
            			years=Integer.valueOf("-"+theString.split("\\-")[1].trim());
            	 datetime= DateTimeUtils.calendarEndDateOfYear(Calendar.getInstance(), years);
           }
		 return datetime;
*/
		
	}
	public static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public static String getTargetCsvFilePath(){
		
		Config instance=Config.getInstance();
		String targetDB=instance.getProperty("TargetDB").toLowerCase();
		switch (targetDB){
			case Config.NTS_Domain_DB:
				return "Conf/DomainTestCases";
			case Config.NTS_UMA_DB:
				return "Conf/UMATestCases";
			case Config.Baby_Fresh_DB:
				return "Conf/FreshTestCases";
			default:
				throw new RuntimeException("ERROR cannot find the target DB");
		}
		
	}
	public static String getTargetExcelFile(){
		switch (Config.getInstance().getProperty("TargetDB").toLowerCase()){
			case Config.NTS_Domain_DB:
				return Config.getInstance().getProperty("NTS_Domain_DB");
			case Config.Baby_Fresh_DB:
				return Config.getInstance().getProperty("Baby_Fresh_DB");
			case Config.NTS_UMA_DB:
				return Config.getInstance().getProperty("NTS_UMA_DB");
			default:
				throw new RuntimeException("ERROR cannot find the target DB");
		}
	}
	public static void GenerateExcel() throws Exception{
         
        /* Step -2 : Define POI Spreadsheet objects */          
        HSSFWorkbook new_workbook = new HSSFWorkbook(); //create a blank workbook object
        files=getFileList();
        for (File file : files) {
        	 CSVReader reader;
        	
        	 FileInputStream fis = new FileInputStream(file.getCanonicalPath());
        	    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        	    reader=  new CSVReader(isr, ',', '\"', '\t'); 
        	 writeToExcelSheet(file.getName().replaceAll(fileExtension, ""), new_workbook,reader );
        }
        
        /* Write XLS converted CSV file to the output file */
        FileOutputStream output_file = new FileOutputStream(getTargetExcelFile()); //create XLS file
        new_workbook.write(output_file);//write converted XLS file to output stream
        output_file.flush();
        output_file.close(); //close the file
        System.gc(); 
    }
}
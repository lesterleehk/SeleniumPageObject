package com.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.config.Config;
import com.controller.TestDriverUtils;
import com.java.utils.DateTimeUtils;
import com.java.utils.ExcelReflectionUtils;
import com.java.utils.URLUtils;
import com.page.test.User;
import com.page.utils.InteractionUtils;





/**Thanks to JUnit4, we can take screenshots after test failures easily by 
 * adding @Rule annotation in test driver (com.netdimen.controller.TestDriver) 
 * 
 * @author lester.li
 *
 */
public class ScreenShotOnFailed extends TestReport {

	private String absolute_FileName;

	public String getAbsolute_FileName() {
		return absolute_FileName;
	}

	public void setAbsolute_FileName(String absolute_FileName) {
		this.absolute_FileName = absolute_FileName;
	}

	public ScreenShotOnFailed() {
		super();
    }

    @Override
    public void failed(Throwable e, Description description) {
        TakesScreenshot takesScreenshot = (TakesScreenshot)getWebDriver();

        File scrFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        File destFile = getDestinationFile( description.getDisplayName() );
        try {
            FileUtils.copyFile(scrFile, destFile);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        TestReport.EXCEL_LOCK.lock();
        SaveFailReportToExcel();
        TestReport.EXCEL_LOCK.unlock();
       
		setAbsolute_FileName("");
		
		// upon fail, we force logout then login default admin
		
		User user = new User(testObject.getUID(), testObject.getPWD());
		user.logout(webDriver);
		user.login(webDriver);
		TestDriverUtils.returnWebDriver(webDriver);
		
    }
    
	public void SaveFailReportToExcel(){
		String sheetName;
	
		sheetName= Config.getInstance().getProperty("test.report.failed.sheet", webDriver);
	
		HSSFSheet sheet = getReportTemplate().getSheet(sheetName);
		if (sheet== null) {
			sheet=getReportTemplate().createSheet(sheetName); 
		}
		IncrementTheCurrentRowOfReportingCase(sheet);
		// method overload in subclass
		writeToExcel(sheet, this.testObject.toString());
	}   
	/**
	 * 
	 * @param Passed = the flag for pass for fail
	 * @param sheet = the excel sheet to write result
	 * @param casename = the case to mark result
	 * @param detail = the detail of fail
	 */
    private void writeToExcel(HSSFSheet sheet, String casename){
		
		HSSFRow row = sheet.getRow(TestReport.getCurrent_row());
		
		
		if (row == null){
			row=sheet.createRow(TestReport.getCurrent_row()); 
		}
	
		
		Cell caseNameCell = row.getCell(0);
		if (caseNameCell == null) caseNameCell = row.createCell(0);
		Cell detailCell;
		detailCell = row.createCell(1);
//		if (TestReport.caseExisted(sheet)){
//			detailCell = row.getCell(1);
//		}else{
//			detailCell = row.createCell(1);
//		}
		
		caseNameCell.setCellValue(casename);
		detailCell.setCellValue("Link:"+generateSheetName());
		
		try {
			if (!getAbsolute_FileName().isEmpty()){ 
				CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
				HSSFSheet sheet2 =createSheetPicture(sheet);
				Hyperlink link2 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
				link2.setAddress("'"+sheet2.getSheetName()+"'!A1");
				//caseNameCell.setHyperlink(link2);
				detailCell.setHyperlink(link2);
				super.setErrorRptCellStyle(sheet);
			}
			FileOutputStream outFile = new FileOutputStream(Config.getInstance().getProperty("test.report.excel", webDriver));
			HSSFWorkbook wb= sheet.getWorkbook();
			wb.write(outFile);
			outFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	}
    private HSSFSheet createSheetPicture(HSSFSheet sheet){
		HSSFSheet my_sheet = sheet.getWorkbook().createSheet(generateSheetName());               
        /* Read the input image into InputStream */
        InputStream my_banner_image;
		try {
			my_banner_image = new FileInputStream(getAbsolute_FileName());
		
	        /* Convert Image to byte array */
	        byte[] bytes = IOUtils.toByteArray(my_banner_image);
	        /* Add Picture to workbook and get a index for the picture */
	        int my_picture_id = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
	        /* Close Input Stream */
	        my_banner_image.close();                
	        /* Create the drawing container */
	        HSSFPatriarch drawing = my_sheet.createDrawingPatriarch();
	        /* Create an anchor point */
	        ClientAnchor my_anchor = new HSSFClientAnchor();
	        /* Define top left corner, and we can resize picture suitable from there */
	        my_anchor.setCol1(2);
	        my_anchor.setRow1(1);           
	        /* Invoke createPicture and pass the anchor point and ID */
	        HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
	        /* Call resize method, which resizes the image */
	        my_picture.resize();            
	        /* Write changes to the workbook */
	        FileOutputStream out = new FileOutputStream(new File(Config.getInstance().getProperty("test.report.excel", webDriver)));
	        sheet.getWorkbook().write(out);
	        out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			   System.out.println(e);
		  }
		return my_sheet;
	}
    @Override
    protected void succeeded(Description description){
    	/*WebDriverUtils.closeAllPopUpWins(driver);
    	Navigator.navigate(driver, Navigator.URL.HomePage);*/
    	
    	// re-use webdriver if test is successful
    	InteractionUtils.closeAllPopUpWins(webDriver);
    	TestDriverUtils.returnWebDriver(webDriver);
    }

    
    @Override
    protected void finished(Description description) {
    	/*WebDriverUtils.closeAllPopUpWins(driver);
    	Navigator.navigate(driver, Navigator.URL.HomePage);*/
    	//webDriver.quit();
    }

    public File getDestinationFile(String name) {
    	String userDirectory = Config.getInstance().getProperty("screenShotDir");
        String fileName = name +"_" + DateTimeUtils.getTimeStamp() +".png";
        absolute_FileName = userDirectory + ExcelReflectionUtils.FILE_PATH_SEPARATOR + fileName;
        File file = new File(absolute_FileName);
        return file;
    }
    private String generateSheetName(){
    	return TestReport.getCurrent_row()+"_"+testObject.getFuncType();
    }
}

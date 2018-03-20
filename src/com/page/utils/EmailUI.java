package com.page.utils;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.config.Config;
import com.java.utils.AES_Cipher;
import com.java.utils.StringUtils;
import com.junit.JUnitAssert;
import com.page.navigate.Navigator;


public class EmailUI {
	
	// Suppress default constructor for noninstantiability
	private EmailUI() {

		throw new AssertionError();
	}

	public static void Send(String subject, String content) {
	      // Recipient's email ID needs to be mentioned.
		  String[] recipients = Config.getInstance().getProperty("mail.recipient").split(",");

	      // Sender's email ID needs to be mentioned
	      String from =  Config.getInstance().getProperty("mail.sender");

	      // Assuming you are sending email from localhost
	      String host = Config.getInstance().getProperty("mail.smtp.host");
	      final String username=Config.getInstance().getProperty("smtp.AuthUserName");
	      final String passw=AES_Cipher.decrypt(Config.getInstance().getProperty("smtp.AuthPassword"));
	      Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
	 
			Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username,passw);
					}
				});
	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         for(String recipient:recipients){
	        	 message.addRecipient(Message.RecipientType.TO,
                         new InternetAddress(recipient));
	         }

	         // Set Subject: header field
	         message.setSubject(subject);

	         // Now set the actual message
	         message.setText(content);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent email successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	}
	
	/**
	 * 
	 * @param driver
	 * @param title: Email Title e.g. Enrollment Confirmation
	 * @param ExpectedContent: Expected email content
	 */

	public static void CheckInternalEmail(WebDriver driver, String title, String ExpectedContent){
			JUnitAssert.assertTrue(checkIfEmailReceived(driver, title),"Fail to receive email:" + title);
			By by = By.xpath("//a[contains(text(),'"+title+"') and descendant::img[contains(@src,'envelop_unread.png')]]");
			InteractionUtils.clickLink(driver, by);
			by=By.xpath("//tr[@class='internal-mail-content']");
			String ActualContent=InteractionUtils.getText(driver, by);
		
			String ActualContentReplaceBlank = StringUtils.replaceBlank(ActualContent).toLowerCase();
			String ExpectedContentReplaceBlank = StringUtils.replaceBlank(ExpectedContent).toLowerCase();
			
			JUnitAssert.assertTrue(ActualContentReplaceBlank.contains(ExpectedContentReplaceBlank), "Email Content is wrong. Expected Result:"+ExpectedContent+";Actual Result:"+ActualContent);		
	}
	
	/**
	 * check if email is received
	 * @param driver
	 * @param EmailTitle
	 * @return
	 */
	public static Boolean checkIfEmailReceived(WebDriver driver, String EmailTitle){
		InteractionUtils.switchToBaseWin(driver);
		Navigator.navigate(driver, Navigator.xmlWebElmtMgr.getNavigationPathList(
				"LearningCenter", "MyEmails", driver),null);
		
		Navigator.waitForAjax(driver,By.id("checkAll"));// add to wait the page load to finish
		By by = By.xpath("//a[contains(text(),'"+EmailTitle+"') and descendant::img[contains(@src,'envelop_unread.png')]]");
		if(InteractionUtils.getHowManyByPresntInPage(driver,by,false)>=1){
			return true;
		} else {
			return false;
		}
		
	}
	
	
	
}

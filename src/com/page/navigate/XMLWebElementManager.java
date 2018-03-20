package com.page.navigate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.config.Config;
import com.java.utils.Validate;


/**
 * 
 * @author lester.li This is a manage class for WebElement XMl Loading
 */
public class XMLWebElementManager {
	
    private Document m_doc;
    private static XMLWebElementManager instance;

	private XMLWebElementManager(String xmlfile)
    {
		try{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File Xml = new File(xmlfile);
        m_doc = builder.parse(new FileInputStream(Xml));
	    } catch (IOException | SAXException | ParserConfigurationException e) {
	  		// TODO Auto-generated catch block
	  		e.printStackTrace();
	  	}
    }
 
	public static XMLWebElementManager getInstance() {
		//String xmlfile = "./Conf/WebElements.xml";
		String xmlfile = System.getProperty("user.dir")+"\\"+ Config.getInstance().getProperty("XMLWebElements");
		if (instance == null) {
			synchronized (XMLWebElementManager.class) { // Add a synch block
				if (instance == null) { // verify some other synch block didn't
										// create a WebElementManager yet...
					instance = new XMLWebElementManager(xmlfile);
				}
			}
		}
		return instance;
	}
	public By getBy(String parentNodeName, String targetChildName, WebDriver driver){
		WebElementWrapper webElm= getWebElementWrapper(parentNodeName,targetChildName, driver);
		return webElm.getBy();
	}
	private int getChildCount(String parentTag, int parentIndex, String childTag)
    {
        NodeList list = m_doc.getElementsByTagName(parentTag);
        Element parent = (Element) list.item(parentIndex);
        NodeList childList = parent.getElementsByTagName(childTag);
        return childList.getLength();
    }
 
    private String getChildValue(String parentTag, int parentIndex, String childTag,
                                int childIndex)
    {
        NodeList list = m_doc.getElementsByTagName(parentTag);
        Element parent = (Element) list.item(parentIndex);
        NodeList childList = parent.getElementsByTagName(childTag);
        Element field = (Element) childList.item(childIndex);
        Node child = field.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

 
    private String getChildAttribute(String parentTag, int parentIndex, 
                                  String childTag, int childIndex,
                                  String attributeTag) {
        NodeList list = m_doc.getElementsByTagName(parentTag);
        Element parent = (Element) list.item(parentIndex);
        NodeList childList = parent.getElementsByTagName(childTag);
        Element element = (Element) childList.item(childIndex);
        return element.getAttribute(attributeTag);
    }
    private synchronized Element getChild(String parentName, String targetChildName) {
    	NodeList list = m_doc.getElementsByTagName(parentName);
    	if (list.getLength()>2 || list.getLength()<1)
    		return null; //Error in xml document as there should be only one parent element in xml files
        ArrayList<Node> child= new ArrayList<Node>();
        visitRecursively(list.item(0), targetChildName, child);
        if (child.size()==0){
        	throw new RuntimeException("Cannot find the targer child element="+targetChildName);
        }
        return (Element)child.get(0);
    }  
    
	/**
	 * 
	 * @param parentNodeName
	 * @param targetChildName
	 * @return
	 */
	public WebElementWrapper getWebElementWrapper(String parentNodeName, String targetChildName, WebDriver driver) {
		return createWebElementWrapperFromXMLElement(getChild(parentNodeName,  targetChildName), driver);
	}
 
	private void visitRecursively(Node parentNode, String targetChildName, ArrayList<Node> child) {

    	// get all child nodes
    	NodeList list = parentNode.getChildNodes();

    	for (int i=0; i<list.getLength(); i++) {
    		// get child node

    		Node childNode = list.item(i);
    		if (childNode.getNodeType() == Node.ELEMENT_NODE && targetChildName.equals(childNode.getNodeName())){
    			//System.out.println("Found Node: " + childNode.getNodeName()+ " - with value: " + childNode.getNodeValue());
    			child.add(childNode);
    		}
    		// visit child node
    		 visitRecursively(childNode, targetChildName, child);
    	}
    	
    }
	private WebElementWrapper createWebElementWrapperFromXMLElement(Element element, WebDriver driver){
		String htmlElementType, seleniumByType="",id, seleniumByValue="";
		String parameter="";
		id=element.getNodeName();
		parameter=element.getAttribute(WebElementWrapper.Parameter);
		if (Validate.isBlank(element.getAttribute(WebElementWrapper.HtmlElementType))){
			htmlElementType="";
		}else{
			htmlElementType=element.getAttribute(WebElementWrapper.HtmlElementType);
		}
		
		boolean topMenu=false;
		if (element.getAttribute("TopMenu").equals(WebElementWrapper.IsTopMenu) ){
			topMenu=true;
		}
		if (element.hasAttribute("XPath")){
			seleniumByType=WebElementWrapper.ByXPath;
			seleniumByValue=element.getAttribute("XPath");
		}
		if (element.hasAttribute("ClassName")){
			seleniumByType=WebElementWrapper.ByClassName;
			seleniumByValue=element.getAttribute("ClassName");
		}
		if (element.hasAttribute("CssSelector")){
			seleniumByType=WebElementWrapper.ByCssSelector;
			seleniumByValue=element.getAttribute("CssSelector");
		}
		if (element.hasAttribute("Id")){
			seleniumByType=WebElementWrapper.ById;
			seleniumByValue=element.getAttribute("Id");
		} 
		if (element.hasAttribute("LinkText")){
			seleniumByType=WebElementWrapper.ByLinkText;
			seleniumByValue=element.getAttribute("LinkText");
		}
		if (element.hasAttribute("Name")){
			seleniumByType=WebElementWrapper.ByName;
			seleniumByValue=element.getAttribute("Name");
		}
		if (element.hasAttribute("TagName")){
			seleniumByType=WebElementWrapper.ByTagName;
			seleniumByValue=element.getAttribute("TagName");
		}
		if (element.hasAttribute("PartialLinkText")){
			seleniumByType=WebElementWrapper.ByPartialLinkText;
			seleniumByValue=element.getAttribute("PartialLinkText");
		}
		if (element.hasAttribute("URL")){
			seleniumByType=WebElementWrapper.URL;
			seleniumByValue=element.getAttribute("URL");
		} 
		return new WebElementWrapper(htmlElementType,id, seleniumByType, seleniumByValue, parameter, topMenu, element.getParentNode().getLocalName(), driver);
		
	}
    public ArrayList<WebElementWrapper> getNavigationPathList(String parentNodeName, String targetChildName, WebDriver driver){
    	
    	ArrayList<WebElementWrapper> list= new ArrayList<WebElementWrapper>();
    	ArrayList<Element> elementsList = new ArrayList<Element>();
    	Element elemt= getChild(parentNodeName, targetChildName);
    	elementsList.add(elemt);
    	do{
    		elemt = (Element)elemt.getParentNode();
    		elementsList.add(elemt);
    	}while(!elemt.getNodeName().equals(parentNodeName));
    	Collections.reverse(elementsList);
    	
    	for (int i=0; i<elementsList.size(); i++) {
    		Element element= (Element)(elementsList.get(i));
    		//if (!element.getNodeName().equals(parentNodeName)){
        		list.add(createWebElementWrapperFromXMLElement(element, driver));
    		//}
    	}
    	return list;
    	
    }
    public static void main(String[] args) {
         try {
//        	 //XMLWebElementManager doc = new XMLWebElementManager("./Conf/WebElements.xml");
//        	 XMLWebElementManager doc = XMLWebElementManager.getInstance();
//        	 //ArrayList<WebElementWrapper> list =doc.getNavigationPathList("ManageCenter", "ScheduleRpt", driver);
//        	 for (WebElementWrapper elem : list){
//        		 System.out.println(elem.getElementValue());
//        	 }
 
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
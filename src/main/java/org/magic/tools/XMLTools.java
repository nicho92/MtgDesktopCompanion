package org.magic.tools;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class XMLTools {

	
	private XMLTools() {}
	
	private static DocumentBuilderFactory dbf;
	
	
	public static DocumentBuilderFactory createSecureXMLFactory()
	{
		try {
			
			if(dbf==null) {
				dbf= DocumentBuilderFactory.newInstance();
		    	dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    	dbf.setXIncludeAware(false);
		    	dbf.setExpandEntityReferences(false);
			}
			return dbf;
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	
	
	public static String parseXML(Document document,String xpath)	throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath().evaluate(xpath, document.getDocumentElement());
	}
	
	
}

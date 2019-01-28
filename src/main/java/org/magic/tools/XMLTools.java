package org.magic.tools;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLTools {

	
	private XMLTools() {};
	
	public static DocumentBuilderFactory createSecureXMLFactory()
	{
		try {
			DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
		    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    dbf.setXIncludeAware(false);
		    dbf.setExpandEntityReferences(false);
			return dbf;
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	
}

package org.magic.services.tools;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLTools {

	private static Logger logger = MTGLogger.getLogger(XMLTools.class);

	private XMLTools() {}


	public static DocumentBuilder createSecureXMLDocumentBuilder() throws ParserConfigurationException
	{
		try {

			var df= DocumentBuilderFactory.newInstance();
		    	df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		    	df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		    	df.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    	df.setXIncludeAware(false);
		    	df.setExpandEntityReferences(false);
		    	return df.newDocumentBuilder();

		}
		catch(Exception e)
		{
			logger.error("Error creating XMLBuilder",e);
			return null;
		}
	}


	public static NodeList parseNodes(Document doc, String expression) throws XPathExpressionException {
		return (NodeList) XPathFactory.newInstance().newXPath().compile(expression).evaluate(doc, XPathConstants.NODESET);
	}

}

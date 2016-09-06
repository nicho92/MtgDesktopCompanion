package org.magic.services;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.ONLINE_PAPER;
import org.magic.gui.CollectionPanelGUI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VersionChecker {

	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	NodeList nodeList;
	
	String urlVersion ="https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/default-conf.xml";
	String actualVersion = MagicFactory.getInstance().get("version");
	String onlineVersion;
	
	static final Logger logger = LogManager.getLogger(VersionChecker.class.getName());

	
	public VersionChecker() {
		
		builderFactory =DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new URL(urlVersion).openStream());
			
			XPath xPath =  XPathFactory.newInstance().newXPath();
			String expression = "//conf/version";
			
			nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Node item = nodeList.item(0);
			
			onlineVersion=item.getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean hasNewVersion()
	{
		try{
			return Double.parseDouble(onlineVersion) > Double.parseDouble(actualVersion);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			return false;
		}
	}

	public String getOnlineVersion() {
		return onlineVersion;
	}

	
	
	
}

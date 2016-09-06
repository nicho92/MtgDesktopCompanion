package org.magic.test;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.magic.api.interfaces.abstracts.AbstractDashBoard.ONLINE_PAPER;
import org.magic.services.MagicFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestVersion {

	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	NodeList nodeList;
	
	String urlVersion ="https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/default-conf.xml";
	String actualVersion = MagicFactory.getInstance().get("version");
	String onlineVersion;
	
	
	
	public TestVersion() {
		
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
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		System.out.println(new TestVersion().hasNewVersion());
			
	}
}

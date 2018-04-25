package org.magic.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class VersionChecker {

	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	NodeList nodeList;

	String actualVersion;
	String onlineVersion;

	Logger logger = MTGLogger.getLogger(this.getClass());

	public VersionChecker() {
		actualVersion = MTGControler.getInstance().getVersion();
		builderFactory = DocumentBuilderFactory.newInstance();
		try {
			InputStream input = new URL(MTGConstants.MTG_DESKTOP_POM_URL).openConnection().getInputStream();
			onlineVersion = parseXML(input);
		} catch (Exception e) {
			onlineVersion = "";
			logger.error(e.getMessage());
		}
	}

	private String parseXML(InputStream input)	throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(input);
		XPathFactory xpf = XPathFactory.newInstance();
		XPath path = xpf.newXPath();
		return path.evaluate("/project/version", doc.getDocumentElement());
	}

	public boolean hasNewVersion() {
		try {
			logger.info("check new version of app " + actualVersion);
			boolean res = Double.parseDouble(onlineVersion) > Double.parseDouble(actualVersion);
			logger.info("check new version of app online " + res + "(" + onlineVersion + ")");

			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public String getOnlineVersion() {
		return onlineVersion;
	}

}

package org.magic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.magic.tools.URLTools;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class VersionChecker {

	private String actualVersion;
	private String onlineVersion;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	public String getVersion() {
		InputStream input = getClass().getResourceAsStream(MTGConstants.MTG_DESKTOP_VERSION_FILE);
		BufferedReader read = new BufferedReader(new InputStreamReader(input));
		try {
			String version = read.readLine();

			if (version.startsWith("${"))
				return "0.0";
			else
				return version;

		} catch (IOException e) {
			return "";
		}

	}

	
	
	public VersionChecker() {
		actualVersion = getVersion();
		try {
			InputStream input =URLTools.openConnection(MTGConstants.MTG_DESKTOP_POM_URL).getInputStream();
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

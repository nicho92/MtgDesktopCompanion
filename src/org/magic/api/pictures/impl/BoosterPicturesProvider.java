package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MagicFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BoosterPicturesProvider {

	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	
	private int w,h;
	static final Logger logger = LogManager.getLogger(BoosterPicturesProvider.class.getName());
	
	public BoosterPicturesProvider() {
		
		w=254;
		h=450;
	
		builderFactory =DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			//document = builder.parse(this.getClass().getResourceAsStream("/res/boosters.xml"));
			logger.debug("Loading booster pics");
			document = builder.parse(new URL("https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/res/boosters.xml").openStream());
			logger.debug("Loading booster pics done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Icon getBoosterFor(MagicEdition me)
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "//booster[contains(@id,'"+me.getId()+"')]";
		NodeList nodeList;
		String url=""; 
			try {
				nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
				Node item = nodeList.item(0);
				url = item.getAttributes().getNamedItem("url").getNodeValue();
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
				return new ImageIcon(ImageIO.read(connection.getInputStream()).getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH));
			} catch (IOException e) {
				logger.error("Could not load : " + url);
				return null;
			} catch (XPathExpressionException e) {
				logger.error(me.getId() + " is not found");
				return null;
			}
			catch (NullPointerException e) {
				logger.error(me.getId() + " is not found");
				return null;
			}
	}
	
	
}

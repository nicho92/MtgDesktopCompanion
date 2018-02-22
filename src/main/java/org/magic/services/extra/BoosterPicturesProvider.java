package org.magic.services.extra;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BoosterPicturesProvider {

	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	
	private int w;
	private int h;
	Logger logger = MTGLogger.getLogger(this.getClass());
	
	public BoosterPicturesProvider() {
		
		w=254;
		h=450;
	
		builderFactory =DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			logger.debug("Loading booster pics");
			document = builder.parse(new URL(MTGConstants.MTG_BOOSTERS_URI).openStream());
			logger.debug("Loading booster pics done");
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public List<String> listEditionsID()
	{
		List<String> list = new ArrayList<>();
		
		try {
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression="//booster/@id";
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		
		for(int i=0;i<nodeList.getLength();i++)
				list.add(nodeList.item(i).getNodeValue());
		}
		catch(Exception e)
		{
			logger.error("Error retrieving IDs ",e);
		}
		return list;
	}
	
	
	public Icon getBoosterFor(MagicEdition me)
	{
	
		String url=""; 
			try {
				XPath xPath =  XPathFactory.newInstance().newXPath();
				String expression = "//booster[contains(@id,'"+me.getId().toUpperCase()+"')]";
				NodeList nodeList;
				nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
				Node item = nodeList.item(0);
				url = item.getAttributes().getNamedItem("url").getNodeValue();
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
				return new ImageIcon(ImageIO.read(connection.getInputStream()).getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH));
			} catch (IOException e) {
				logger.error(me.getId() + " could not load : " + url + ":" + e);
				return null;
			} catch (XPathExpressionException e) {
				logger.error(me.getId() + " is not found :" + e);
				return null;
			}
			catch (Exception e) {
				logger.error(me.getId() + " error loading " + url + " " + e.getMessage());
				return null;
			}
	}
	
	
}

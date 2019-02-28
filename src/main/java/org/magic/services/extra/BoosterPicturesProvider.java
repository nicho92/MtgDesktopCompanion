package org.magic.services.extra;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BoosterPicturesProvider {

	private Document document;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	public enum LOGO { ORANGE,BLUE,YELLOW,WHITE,NEW}
	private List<String> list;
	
	public BoosterPicturesProvider() {
		
		try {
			logger.debug("Loading booster pics");
			document = URLTools.extractXML(MTGConstants.MTG_BOOSTERS_URI);
			logger.debug("Loading booster pics done");
			list = new ArrayList<>();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	


	public List<String> listEditionsID() {

		if (!list.isEmpty())
			return list;

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "//booster/@id";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++)
				list.add(nodeList.item(i).getNodeValue());
			
		} catch (Exception e) {
			logger.error("Error retrieving IDs ", e);
		}
		return list;
	}

	public Map<String, URL> getBoostersUrl(MagicEdition me)
	{
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "//booster[contains(@id,'" + me.getId().toUpperCase() + "')]/pack";
		logger.trace(expression);
		
		NodeList liste;
		try {
			liste = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			logger.error(me.getId() + " not found :" + e);
			return null;
		}
		
		HashMap<String, URL> ret = new HashMap<>();
		try {
			for(int i=0;i<liste.getLength();i++)
			{
				String id = //liste.item(i).getAttributes().getNamedItem("lang").getNodeValue()+"-"+
							liste.item(i).getAttributes().getNamedItem("num").getNodeValue();
				
				ret.put(id,new URL(liste.item(i).getAttributes().getNamedItem("url").getNodeValue()));
			}
		} catch (Exception e) {
			logger.error("error loading", e);
			return null;
		}
		return ret;

	}
	
	public BufferedImage getBannerFor(MagicEdition me)
	{
		return get(me,"banner");
	}
	
	public BufferedImage getBannerFor(String idMe)
	{
		return get(new MagicEdition(idMe),"banner");
	}
	
	public BufferedImage getBoxFor(String idMe)
	{
		return getBoxFor(new MagicEdition(idMe));
	}
	
	public BufferedImage getBoxFor(MagicEdition me) {
		return get(me,"box");
	}

	public BufferedImage getStarterFor(String idMe)
	{
		return getStarterFor(new MagicEdition(idMe));
	}
	
	public BufferedImage getStarterFor(MagicEdition me) {
		return get(me,"starter");
	}
	


	public BufferedImage getLogo(LOGO logo)
	{
		String url = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "//logo[contains(@version,'" + logo.name().toLowerCase() + "')]";
			logger.trace(expression);
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Node item = nodeList.item(0);
			url = item.getAttributes().getNamedItem("url").getNodeValue();
			return URLTools.extractImage(url);
		} catch (IOException e) {
			logger.error(logo + " could not load : " + url,e);
			return null;
		} catch (XPathExpressionException e) {
			logger.error(logo + " is not found :" + e);
			return null;
		} catch (Exception e) {
			logger.error(logo + " error loading " + url,e);
			return null;
		}
	}
	

	
	private BufferedImage get(MagicEdition me,String k) {
		String url = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "//booster[contains(@id,'" + me.getId().toUpperCase() + "')]/"+k;
			logger.trace(expression);
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Node item = nodeList.item(0);
			url = item.getAttributes().getNamedItem("url").getNodeValue();
			return URLTools.extractImage(url);
		} catch (IOException e) {
			logger.error(me.getId() + " could not load : " + url,e);
			return null;
		} catch (XPathExpressionException e) {
			logger.error(me.getId() + " is not found :" + e);
			return null;
		} catch (Exception e) {
			logger.error(me.getId() + " error loading " + url,e);
			return null;
		}
	}
	

}

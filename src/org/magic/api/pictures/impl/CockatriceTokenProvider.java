package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MagicCard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CockatriceTokenProvider {

	String url = "https://raw.githubusercontent.com/Cockatrice/Magic-Token/master/tokens.xml";
	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	
	
	public CockatriceTokenProvider() {
		builderFactory =DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new URL(url).openStream() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public BufferedImage getEmblem(MagicCard tok) throws Exception {
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		
		String expression = "//card[name='"+tok.getName()+" (emblem)']";
		
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		Map<String,String> map = null;
		
		for (int i = 0; i < nodeList.getLength(); i++) {
				Element value = (Element) nodeList.item(i);
		/*		String name = value.getElementsByTagName("name").item(0).getTextContent();
				
				String color="";
				if(value.getElementsByTagName("color").item(0)!=null)
					color = value.getElementsByTagName("color").item(0).getTextContent();
				
				String text = "";
				if(value.getElementsByTagName("text").item(0)!=null)
					value.getElementsByTagName("text").item(0).getTextContent();
				*/
				NodeList sets = value.getElementsByTagName("set");
				map = new HashMap<>();
				for (int s = 0; s < sets.getLength(); s++) {
					String set = sets.item(s).getTextContent();
					String pic = "";
					if(sets.item(s).getAttributes().getNamedItem("picURL")!=null)
						pic = sets.item(s).getAttributes().getNamedItem("picURL").getNodeValue();

					map.put(set, pic);
				}
		}
		URLConnection connection = new URL(map.get(tok.getEditions().get(0).getId())).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
		
		return ImageIO.read(connection.getInputStream());
	}
	
	
	public BufferedImage getToken(MagicCard tok) throws Exception {
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		
		String expression = "//card[name='"+tok.getName()+"']";
		
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		Map<String,String> map = null;
		
		for (int i = 0; i < nodeList.getLength(); i++) {
				Element value = (Element) nodeList.item(i);
				/*String name = value.getElementsByTagName("name").item(0).getTextContent();
				
				String color="";
				if(value.getElementsByTagName("color").item(0)!=null)
					color = value.getElementsByTagName("color").item(0).getTextContent();
				
				String manacost = value.getElementsByTagName("manacost").item(0).getTextContent();
				String type = value.getElementsByTagName("type").item(0).getTextContent();
				String pt = "";
				if(value.getElementsByTagName("pt").item(0)!=null)
					value.getElementsByTagName("pt").item(0).getTextContent();
				
				String text = "";
				if(value.getElementsByTagName("text").item(0)!=null)
					value.getElementsByTagName("text").item(0).getTextContent();
				*/
				NodeList sets = value.getElementsByTagName("set");
				map = new HashMap<>();
				for (int s = 0; s < sets.getLength(); s++) {
					String set = sets.item(s).getTextContent();
					String pic = "";
					if(sets.item(s).getAttributes().getNamedItem("picURL")!=null)
						pic = sets.item(s).getAttributes().getNamedItem("picURL").getNodeValue();
					
					map.put(set, pic);
				}
		}
		URLConnection connection = new URL(map.get(tok.getEditions().get(0).getId())).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
		
		return ImageIO.read(connection.getInputStream());
	}

}

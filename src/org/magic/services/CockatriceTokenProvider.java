package org.magic.services;

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
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.tools.ColorParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CockatriceTokenProvider {

	String url = "https://raw.githubusercontent.com/Cockatrice/Magic-Token/master/tokens.xml";
	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	static final Logger logger = LogManager.getLogger(CockatriceTokenProvider.class.getName());
	
	public CockatriceTokenProvider() {
		builderFactory =DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new URL(url).openStream() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isTokenizer(MagicCard mc)
	{
		String expression = "//card[reverse-related=\""+mc.getName()+"\"][not(contains(name,'emblem'))]";
		logger.debug("looking for token : " + expression);
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return (nodeList.getLength()>0);
		} catch (XPathExpressionException e) {
			return false;
		}
	}
	
	public boolean isEmblemizer(MagicCard mc)
	{
		String expression = "//card[reverse-related=\""+mc.getName()+"\"][contains(name,'emblem')]";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			return (nodeList.getLength()>0);
		} catch (XPathExpressionException e) {
			return false;
		}
	}
	
	public MagicCard generateTokenFor(MagicCard mc) {
		String expression = "//card[reverse-related=\""+mc.getName()+"\"][not(contains(name,'emblem'))]";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
				Element value = (Element) nodeList.item(0);
				MagicCard tok = new MagicCard();
						  tok.setLayout(MagicCard.LAYOUT.Token.toString());
						  tok.setCmc(0);
						  
						  tok.setName(value.getElementsByTagName("name").item(0).getTextContent());
						 
						  if(value.getElementsByTagName("color").item(0)!=null)
						  {
							  tok.getColors().add(ColorParser.getNameByCode(value.getElementsByTagName("color").item(0).getTextContent()));
						  	  tok.getColorIdentity().add("{"+value.getElementsByTagName("color").item(0).getTextContent()+"}");
						  }
							
						  
						  String types = value.getElementsByTagName("type").item(0).getTextContent();
						  
						  
						  if(types.toLowerCase().contains("legendary"))
							  tok.getSupertypes().add("Legendary");
						  
						  if(types.toLowerCase().contains("artifact"))
							  tok.getTypes().add("Artifact");
						  
						  if(types.toLowerCase().contains("creature"))
							  tok.getTypes().add("Creature");
						  
						  tok.getTypes().add(MagicCard.LAYOUT.Token.toString());
							
						  
						  tok.getSubtypes().add(types.substring(types.indexOf("—")+1));
						  
						  if(value.getElementsByTagName("pt").item(0)!=null)
						  {
							  tok.setPower(value.getElementsByTagName("pt").item(0).getTextContent().substring(0, value.getElementsByTagName("pt").item(0).getTextContent().indexOf("/")).trim());
							  tok.setToughness(value.getElementsByTagName("pt").item(0).getTextContent().substring(value.getElementsByTagName("pt").item(0).getTextContent().indexOf("/")+1).trim());
						  }
						  if(value.getElementsByTagName("text").item(0)!=null)		
							  tok.setText(value.getElementsByTagName("text").item(0).getTextContent());
						  
						  tok.getEditions().add(mc.getEditions().get(0));
						  tok.setNumber("T");
						  
						  NodeList sets = value.getElementsByTagName("set");
						  for (int s = 0; s < sets.getLength(); s++) {
							  String idSet = sets.item(s).getTextContent();
							  
							  if(idSet.equals(mc.getEditions().get(0).getId()))
							  try {
								  MagicEdition ed = new MagicEdition();
								  ed=MTGControler.getInstance().getEnabledProviders().getSetById(idSet);
								  tok.getEditions().add(ed);
							} catch (Exception e) {}
							  
						  }
						  return tok;
			
		} catch (XPathExpressionException e) {
			logger.error(e);
			return null;
		}
	}

	public MagicCard generateEmblemFor(MagicCard mc) throws Exception {
		String expression = "//card[reverse-related=\""+mc.getName()+"\"][contains(name,'emblem')]";
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
				Element value = (Element) nodeList.item(0);
				MagicCard tok = new MagicCard();
						  tok.setLayout(MagicCard.LAYOUT.Emblem.toString());
						  tok.setCmc(0);
						  tok.setName(value.getElementsByTagName("name").item(0).getTextContent().replaceAll("\\(emblem\\)", "").trim());
						  String types = value.getElementsByTagName("type").item(0).getTextContent();
						  tok.getSupertypes().add(MagicCard.LAYOUT.Emblem.toString());
						  tok.getSubtypes().add(types.substring(types.indexOf("—")+1));
						  tok.setText(value.getElementsByTagName("text").item(0).getTextContent());
						  tok.setNumber("E");
						  
						  /* NodeList sets = value.getElementsByTagName("set");
						  for (int s = 0; s < sets.getLength(); s++) {
							  String idSet = sets.item(s).getTextContent();
							  
							  if(idSet.equals(mc.getEditions().get(0).getId()))
							  try {
								  MagicEdition ed = new MagicEdition();
								  ed=MTGControler.getInstance().getEnabledProviders().getSetById(idSet);
								  tok.getEditions().add(ed);
							} catch (Exception e) {}
							  
						  }*/
						  tok.getEditions().add(mc.getEditions().get(0));
						  return tok;
			
		} catch (XPathExpressionException e) {
			return null;
		}
	}
	
	public BufferedImage getPictures(MagicCard tok) throws Exception {
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		String expression = "//card[name=\""+tok.getName()+"\"]";
		
		if(tok.getLayout().equals(MagicCard.LAYOUT.Emblem.toString()))
			expression ="//card[name=\""+tok.getName()+" (emblem)\"]";
		
		logger.debug(expression + " for " + tok.getEditions().get(0));
		
		
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
		Map<String,String> map = null;
		
		for (int i = 0; i < nodeList.getLength(); i++) {
				Element value = (Element) nodeList.item(i);
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
	
		try {
			URLConnection connection;
			if(map.get(tok.getEditions().get(0).getId())!=null)
				connection = new URL(map.get(tok.getEditions().get(0).getId())).openConnection();
			else
				connection = new URL(map.get(map.keySet().iterator().next())).openConnection();
			
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
		return ImageIO.read(connection.getInputStream());
		}
		catch(Exception e)
		{
			
		throw new Exception("Could not find token for " + tok.getName() + " : " + e);
		}
	}

}

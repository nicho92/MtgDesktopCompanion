package org.magic.services.extra;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.Packaging.TYPE;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.ImageTools;
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
			//document = URLTools.extractXML(MTGConstants.MTG_BOOSTERS_LOCAL_URI);
			document = URLTools.extractXML(MTGConstants.MTG_BOOSTERS_URI);
			logger.debug("Loading booster pics done");
			list = new ArrayList<>();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public List<Packaging> getItemsFor(String me)
	{
		return getItemsFor(new MagicEdition(me));
	}
	
	
	public void caching(boolean force, String s)
	{
		getItemsFor(s).forEach(p->caching(force, p));
	}
	
	public BufferedImage caching(boolean force, Packaging p) {
		
		
		File f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "packaging",p.getEdition().getId().replace("CON", "CON_"),p.getType().name()).toFile();
		File pkgFile = new File(f,p.toString()+".png");
		
		try {
			FileUtils.forceMkdir(f);
			if(force||!pkgFile.exists())
			{
				BufferedImage im = URLTools.extractImage(p.getUrl());
				ImageTools.saveImage(im, pkgFile, "PNG");
				logger.debug("[" + p.getEdition().getId() +"] SAVED for " + p.getType()+"-"+p);
				return im;
			}
		} catch (Exception e) {
			logger.error("[" + p.getEdition().getId() +"] ERROR for " + p.getType()+"-"+p +" :" +e);
		}
		return null;
		
	}

	public BufferedImage get(Packaging p)
	{
		try {
			File b=Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "packaging",p.getEdition().getId().replace("CON", "CON_"),p.getType().name(),p.toString()+".png").toFile();
			
			if(b.exists())
				return ImageIO.read(b);
			else
				return caching(false, p);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}
	
	
	
	public void caching(boolean force)
	{
		listEditionsID().forEach(s->caching(force,s));
	}
	

	public List<Packaging> getItemsFor(MagicEdition me)
	{
		List<Packaging> ret = new ArrayList<>();
		NodeList n = null ;
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.compile("//edition[contains(@id,'" + me.getId().toUpperCase() + "')]").evaluate(document, XPathConstants.NODESET);
			n = nodeList.item(0).getChildNodes();
			
		} catch (Exception e) {
			logger.error("Error retrieving IDs "+ me + " : " + e.getMessage());
		}
		
		if(n==null)
			return ret;
		
		
		for (int i = 0; i < n.getLength(); i++)
		{
			if(n.item(i).getNodeType()==1)
			{
				Packaging p = new Packaging();
						  p.setType(TYPE.valueOf(n.item(i).getNodeName().toUpperCase()));
						  p.setLang(n.item(i).getAttributes().getNamedItem("lang").getNodeValue());
						  p.setUrl(n.item(i).getAttributes().getNamedItem("url").getNodeValue());
						  p.setEdition(me);
						 try {
						  p.setNum(Integer.parseInt(n.item(i).getAttributes().getNamedItem("num").getNodeValue()));
						 }
						 catch(Exception e)
						 {
							 p.setNum(1);
						 }
				
				ret.add(p);
			}
		}
		
		
		return ret;
	}


	public List<String> listEditionsID() {

		if (!list.isEmpty())
			return list;

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "//edition/@id";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++)
				list.add(nodeList.item(i).getNodeValue());
			
		} catch (Exception e) {
			logger.error("Error retrieving IDs ", e);
		}
		return list;
	}

	public List<Packaging> get(MagicEdition me,TYPE t)
	{
		return getItemsFor(me).stream().filter(e->e.getType()==t).collect(Collectors.toList());
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
	

	

}

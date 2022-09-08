package org.magic.services.providers;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGSealedProduct.EXTRA;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.tools.ImageTools;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SealedProductProvider {

	private static final String PACKAGING_DIR_NAME = "packaging";
	private Document document;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	public enum LOGO { ORANGE,BLUE,YELLOW,WHITE,NEW}
	private List<MagicEdition> list;
	private static SealedProductProvider inst;
	
	private SealedProductProvider() {
		try {
			reload();
			list = new ArrayList<>();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	

	public void reload() throws IOException
	{
		logger.debug("Loading sealed data");
		document = URLTools.extractAsXml(MTGConstants.MTG_BOOSTERS_URI);
		logger.debug("Loading sealed data done");
		
		if(list!=null)
			list.clear();
	}

	public List<MTGSealedProduct> search(String name)
	{
		
		var ret = new ArrayList<MTGSealedProduct>();
		listEditions().stream().filter(me->me.getSet().toLowerCase().contains(name.toLowerCase())).toList().forEach(me->ret.addAll(getItemsFor(me)));
		
		return ret;
	}
	
	
	public static SealedProductProvider inst()
	{
		if(inst==null)
			inst=new SealedProductProvider();
		
		return inst;
	}
	
	public List<MTGSealedProduct> getItemsFor(String me)
	{
		return getItemsFor(new MagicEdition(me));
	}
	
	
	public void caching(boolean force, MagicEdition s)
	{
		getItemsFor(s).forEach(p->caching(force, p));
	}
	
	public BufferedImage caching(boolean force, MTGSealedProduct p) {
		
		if(p.getUrl()==null)
			return null;
		
		var f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME,p.getEdition().getId().replace("CON", "CON_"),p.getTypeProduct().name()).toFile();
		var pkgFile = new File(f,p.toString()+".png");
		
		try {
			FileUtils.forceMkdir(f);
			if(force||!pkgFile.exists())
			{
				BufferedImage im = URLTools.extractAsImage(p.getUrl());
				ImageTools.saveImage(im, pkgFile, "PNG");
				logger.debug("[" + p.getEdition().getId() +"] SAVED for " + p.getTypeProduct()+"-"+p);
				return im;
			}
		} catch (Exception e) {
			logger.error("[" + p.getEdition().getId() +"] ERROR for " + p.getTypeProduct()+"-"+p +" :" +e);
		}
		return null;
		
	}
	
	public BufferedImage getLogo(LOGO logo)
	{
		var url = "";
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			var expression = "//logo[contains(@version,'" + logo.name().toLowerCase() + "')]";
			logger.trace(expression);
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			var item = nodeList.item(0);
			url = item.getAttributes().getNamedItem("url").getNodeValue();
			return URLTools.extractAsImage(url);
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


	public BufferedImage get(MTGSealedProduct p)
	{
		try {
			var b=Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME,p.getEdition().getId().replace("CON", "CON_"),p.getTypeProduct().name(),p.toString()+".png").toFile();
			
			if(b.exists())
				return ImageTools.read(b);
			else
				return caching(false, p);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}
	

	public void clear() {
		var f = Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), PACKAGING_DIR_NAME).toFile();
		try {
			FileUtils.cleanDirectory(f);
		} catch (IOException e) {
			logger.error("error removing data in "+f,e);
		}
	}
	
	public void caching(boolean force)
	{
		listEditions().forEach(s->caching(force,s));
	}
	
	public synchronized List<MTGSealedProduct> getItemsFor(MagicEdition me)
	{
		List<MTGSealedProduct> ret = new ArrayList<>();
		
		if(me==null)
			return ret;
		
		NodeList n = null ;
		NodeList nodeList = null;
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			nodeList = (NodeList) xPath.compile("//edition[@id='" + me.getId().toUpperCase() + "']").evaluate(document, XPathConstants.NODESET);
			n = nodeList.item(0).getChildNodes();
			
		} catch (Exception e) {
			logger.trace("Error retrieving IDs "+ me.getId() + "->" + me + " : " + e);
		}
		
		if(n==null)
			return ret;
		
		
		for (var i = 0; i < n.getLength(); i++)
		{
			if(n.item(i).getNodeType()==1)
			{
				var p = new MTGSealedProduct();
						  p.setTypeProduct(EnumItems.valueOf(n.item(i).getNodeName().toUpperCase()));
						 
						  try {
							  p.setLang(n.item(i).getAttributes().getNamedItem("lang").getNodeValue());
						  }
						  catch(Exception e)
						  {
							  logger.error("no lang found for " + p + n.item(i),e);
						  }
						  
						  
						  try {
							  p.setExtra(EXTRA.valueOf(n.item(i).getAttributes().getNamedItem("extra").getNodeValue().toUpperCase()));
						  } 
						  catch (Exception e) {
								//do nothing
						  }
						  
						  
						  p.setUrl(n.item(i).getAttributes().getNamedItem("url").getNodeValue());
						  p.setEdition(me);
						 try {
						  p.setNum(Integer.parseInt(n.item(i).getAttributes().getNamedItem("num").getNodeValue()));
						 }
						 catch(Exception e)
						 {
							 p.setNum(1);
						 }
				
				p.setName(p.getTypeProduct() +" " + p.getEdition());
				ret.add(p);
			}
		}
		
		
		return ret;
	}


	public List<MagicEdition> listEditions() {

		if (!list.isEmpty())
			return list;

		try {
			var xPath = XPathFactory.newInstance().newXPath();
			var expression = "//edition/@id";
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			for (var i = 0; i < nodeList.getLength(); i++)
			{
				list.add(getEnabledPlugin(MTGCardsProvider.class).getSetById(nodeList.item(i).getNodeValue()));
			}
			
			Collections.sort(list);
		} catch (Exception e) {
			logger.error("Error retrieving IDs ", e);
		}
		return list;
	}

	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, String lang, EXTRA extra)
	{
		return get(me,t).stream().filter(e->e.getLang().equalsIgnoreCase(lang) && e.getExtra()==extra).toList();
	}
	
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, String lang)
	{
		return get(me,t).stream().filter(e->e.getLang().equalsIgnoreCase(lang)).toList();
	}
	
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t, EXTRA extra)
	{
		if(extra==null)
			return get(me,t);
		
		return get(me,t).stream().filter(e->e.getExtra()==extra).toList();
	}
	
	public List<MTGSealedProduct> get(MagicEdition me,EnumItems t)
	{
		return getItemsFor(me).stream().filter(e->e.getTypeProduct()==t).toList();
	}

}

package org.magic.api.sealedprovider.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractSealedProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MTGCompanionSealedProvider extends AbstractSealedProvider{

	private Document document;
	public enum LOGO { ORANGE,BLUE,YELLOW,WHITE,NEW}

	
	private void init() 
	{
		
		if(document==null) {
			logger.debug("Loading sealed data");
			try {
				document = URLTools.extractAsXml(MTGConstants.MTG_BOOSTERS_URI);
				logger.debug("Loading sealed data done");
			} catch (IOException e) {
				logger.error("Error while loading data from {}",MTGConstants.MTG_BOOSTERS_URI,e);
			}
			
		}
	}

	public List<MTGSealedProduct> search(String name)
	{
		init();
		var ret = new ArrayList<MTGSealedProduct>();
		try {
			MTG.getEnabledPlugin(MTGCardsProvider.class).listEditions().stream().filter(me->me.getSet().toLowerCase().contains(name.toLowerCase())).toList().forEach(me->ret.addAll(getItemsFor(me)));
		} catch (IOException e) {
			logger.error(e);
		}

		return ret;
	}

	public BufferedImage getLogo(LOGO logo)
	{
		init();
		var url = "";
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			var expression = "//logo[contains(@version,'" + logo.name().toLowerCase() + "')]";
			var nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			var item = nodeList.item(0);
			url = item.getAttributes().getNamedItem("url").getNodeValue();
			return URLTools.extractAsImage(url);
		} catch (IOException e) {
			logger.error("{} could not load : {}",logo,url,e);
			return null;
		} catch (XPathExpressionException e) {
			logger.error("{} is not found :",logo,e);
			return null;
		} catch (Exception e) {
			logger.error("{} error loading {}",logo,url,e);
			return null;
		}
	}


	public synchronized List<MTGSealedProduct> getItemsFor(MagicEdition me)
	{
		List<MTGSealedProduct> ret = new ArrayList<>();
		if(me==null)
			return ret;

		init();
		
		NodeList n = null ;
		NodeList nodeList = null;
		try {
			var xPath = XPathFactory.newInstance().newXPath();
			nodeList = (NodeList) xPath.compile("//edition[@id='" + me.getId().toUpperCase() + "']").evaluate(document, XPathConstants.NODESET);
			n = nodeList.item(0).getChildNodes();

		} catch (Exception e) {
			logger.trace("Error retrieving IDs {}->{} : {}",me.getId(),me,e);
		}

		if(n==null)
			return ret;


		for (var i = 0; i < n.getLength(); i++)
		{
			if(n.item(i).getNodeType()==1)
			{
						var p = new MTGSealedProduct();
						 
						  p.setEdition(me);
						 
						  try {
							p.setTypeProduct(EnumItems.valueOf(n.item(i).getNodeName().toUpperCase()));
						  }
						  catch(Exception e)
						  {
							  logger.error("No EnumItems found for {}", n.item(i).getNodeName());
						  }
						  
						  try {
							  p.setLang(n.item(i).getAttributes().getNamedItem("lang").getNodeValue());
						  }
						  catch(Exception e)
						  {
							  logger.error("no lang found for {},{}",p.getEdition(),p.getTypeProduct());
						  }

						  try {
							  p.setExtra(EnumExtra.valueOf(n.item(i).getAttributes().getNamedItem("extra").getNodeValue().toUpperCase()));
						  }
						  catch (Exception e) {
							 //do nothing
						  }
						  
						  try {
							  p.setUrl(n.item(i).getAttributes().getNamedItem("url").getNodeValue());
						  }
						  catch (Exception e) {
								//do nothing
						  }

						 try {
						  p.setNum(Integer.parseInt(n.item(i).getAttributes().getNamedItem("num").getNodeValue()));
						 }
						 catch(Exception e)
						 {
							 p.setNum(1);
						 }
						 
						 try {
							  p.setName(n.item(i).getAttributes().getNamedItem("desc").getNodeValue());
						 }
						 catch(Exception e)
						 {
							 p.setName(p.getEdition() + " " + p.getTypeProduct());
						 }
							  
						 
						 
					
								
			
				ret.add(p);
			}
		}


		return ret;
	}

	@Override
	public String getName() {
		return MTGConstants.MTG_APP_NAME;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_PACKAGE;
	}
	

}

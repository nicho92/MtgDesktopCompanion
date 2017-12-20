package org.magic.api.pricers.impl;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TCGPlayerPricer extends AbstractMagicPricesProvider {
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
	public TCGPlayerPricer() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
		props.put("MAX", "-1");
		props.put("API_KEY", "MGCASSTNT");
		props.put("URL", "http://partner.tcgplayer.com/x3/phl.asmx/p?v=3&pk=%API_KEY%&s=%SET%&p=%CARTE%");
		props.put("WEBSITE", "http://www.tcgplayer.com/");
		props.put("ENCODING", "UTF-8");
		props.put("KEYWORD", "");
		save();
		}
		
		
		
	}
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
		List<MagicPrice> list = new ArrayList<MagicPrice>();
		String url = props.getProperty("URL");
			   url = url.replaceAll("%API_KEY%", props.getProperty("API_KEY"));
		
		String set = "";
		
		if(me==null)
			set = URLEncoder.encode(card.getEditions().get(0).getSet(),props.getProperty("ENCODING"));
		else
			set = URLEncoder.encode(me.getSet(),props.getProperty("ENCODING"));
		
		if(set.contains("Edition"))
			set =set.replaceAll("Edition", "");
		
		
		String name = card.getName();
			   name = name.replaceAll(" \\(.*$", "");
			   name = name.replaceAll("'", "%27");
			   name = name.replaceAll(" ", "+");
		
			   
			   
			   
			   
			   props.put("KEYWORD", "s="+set+"p="+name);
			   
			   
		String link=url.replaceAll("%SET%", set);
			   link=link.replaceAll("%CARTE%", name);

			   
			   logger.info(getName()  + " looking "+ " for " + link);
		
			   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			   
			   
			   Document doc = null; 
					   
					try{   
					  doc=dBuilder.parse(new URL(link).openStream());
					  logger.debug(doc);
					   
					
			   doc.getDocumentElement().normalize();
			   
			   NodeList nodes = doc.getElementsByTagName("product");
			   
	 		   MagicPrice mp = new MagicPrice();
			   	mp.setCurrency("$");
			   	mp.setSite(getName());
			   	mp.setUrl(nodes.item(0).getChildNodes().item(11).getTextContent());
			   	mp.setSeller(getName());
			   	mp.setValue(Double.parseDouble(nodes.item(0).getChildNodes().item(7).getTextContent()));
			   	
			   	
			   	list.add(mp);
			   	logger.info(getName() +" found " + list.size() +" item(s)" );
			    if(list.size()>Integer.parseInt(props.get("MAX").toString()))
					 if(Integer.parseInt(props.get("MAX").toString())>-1)
						 return list.subList(0, Integer.parseInt(props.get("MAX").toString()));
			   	
				 
					}
					catch(Exception e)
					{
						logger.error(e);
						return list;
					}
					
			    
		return list;

	}

	@Override
	public String getName() {
		return "TCGPlayer";
	}

	@Override
	public void alertDetected(List<MagicPrice> p) {
		// TODO Auto-generated method stub
		
	}
	
}

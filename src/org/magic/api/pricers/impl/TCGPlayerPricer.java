package org.magic.api.pricers.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TCGPlayerPricer implements MagicPricesProvider {

	static final Logger logger = LogManager.getLogger(TCGPlayerPricer.class.getName());
	  
	Properties props;
	
	public TCGPlayerPricer() {
		props = new Properties();
		
		props.put("MAX", -1);
		props.put("API_KEY", "MGCASSTNT");
		props.put("URL", "http://partner.tcgplayer.com/x3/phl.asmx/p?v=3&pk=%API_KEY%&s=%SET%&p=%CARTE%");
		props.put("WEBSITE", "http://www.tcgplayer.com/");
		props.put("ENCODING", "UTF-8");
		props.put("KEYWORD", "");
	}
	
	public String toString()
	{
		return getName();
	}
	
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {

		String url = props.getProperty("URL");
			   url = url.replaceAll("%API_KEY%", props.getProperty("API_KEY"));
		
		String set = "";
		
		if(me==null)
			set = URLEncoder.encode(card.getEditions().get(0).getSet(),props.getProperty("ENCODING"));
		else
			set = URLEncoder.encode(me.getSet(),props.getProperty("ENCODING"));
		
				
		
		
		String name = card.getName();
			   name = name.replaceAll(" \\(.*$", "");
			   name = name.replaceAll("'", "%27");
			   name = name.replaceAll(" ", "+");
			   
			   props.put("KEYWORD", "s="+set+"p="+name);
			   
			   
		String link=url.replaceAll("%SET%", set);
			   link=link.replaceAll("%CARTE%", name);

			   
			   logger.debug(getName()  + " looking "+ " " + link);
		
			   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			   Document doc = dBuilder.parse(link);

			   doc.getDocumentElement().normalize();
			   
			   
			   
			   NodeList nodes = doc.getElementsByTagName("product");
			   
	 		   MagicPrice mp = new MagicPrice();
			   	mp.setCurrency("$");
			   	mp.setSite(getName());
			   	mp.setUrl(nodes.item(0).getChildNodes().item(11).getTextContent());
			   	mp.setSeller(getName());
			   	mp.setValue(Double.parseDouble(nodes.item(0).getChildNodes().item(7).getTextContent()));
			   	
			   	List<MagicPrice> list = new ArrayList<MagicPrice>();
			   	list.add(mp);
			   	
			   	if(((int)props.get("MAX"))!=-1)
			   		return list.subList(0, (int)props.get("MAX"));
			   	
			   	
		return list;

	}

	@Override
	public String getName() {
		return "Trading Card Game";
	}
	
	@Override
	public Properties getProperties() {
		return props;
	}
	@Override
	public void setProperties(String k, Object value) {
		props.put(k, value);
		
	}
	@Override
	public Object getProperty(String k) {
		return props.get(k);
	}
	
}

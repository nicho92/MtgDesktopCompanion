package org.magic.api.pricers.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
	String url = "http://partner.tcgplayer.com/x3/phl.asmx/p?v=3&pk=MGCASSTNT&s=%SET%&p=%CARTE%";
	static final Logger logger = LogManager.getLogger(TCGPlayerPricer.class.getName());
	  
	int max=-1;
	
	public String toString()
	{
		return getName();
	}
	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {

		String set = URLEncoder.encode(me.getSet(),"UTF-8");
		String name = card.getName();
			   name = name.replaceAll(" \\(.*$", "");
			   name = name.replaceAll("'", "%27");
			   name = name.replaceAll(" ", "+");
			   
			   
		String link=url.replaceAll("%SET%", set);
			   link=link.replaceAll("%CARTE%", name);

			   
			   logger.debug("Get Price " + getName() + " " + link);
		
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
			   	
			   	if(max!=-1)
			   		return list.subList(0, max);
			   	
			   	
		return list;

	}

	@Override
	public String getName() {
		return "Trading Card Game";
	}
	
	public static void main(String[] args) throws Exception {
		
		MagicCard mc = new MagicCard();
			mc.setName("Bloodstone Cameo");
			
		MagicEdition ed = new MagicEdition();
			ed.setSet("Invasion");
			mc.getEditions().add(ed);
			
		new TCGPlayerPricer().getPrice(ed, mc);
	}

	@Override
	public void setMaxResults(int max) {
		this.max=max;
		
	}
}

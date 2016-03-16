package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;

public class MagicVillePricer implements MagicPricesProvider {

	Document doc;
	List<MagicPrice> list;
	CloseableHttpClient httpclient;
	private boolean enable=true;	
	Properties props;
	
	
	static final Logger logger = LogManager.getLogger(MagicVillePricer.class.getName());

	public MagicVillePricer() {

		list=new ArrayList<MagicPrice>();
		httpclient = HttpClients.createDefault();
		
		props = new Properties();
		
		props.put("MAX", 5);
		props.put("URL", "http://www.magic-ville.com/fr/register/show_card_sale.php?gamerid=");
		props.put("WEBSITE", "http://www.magic-ville.com/");
		props.put("KEYWORD", "");	
		
	}
	
//	public static void main(String[] args) throws IOException {
//		MagicVillePricer pricer = new MagicVillePricer();
//		MagicEdition ed = new MagicEdition();
//		ed.setId("CNS");
//		
//		MagicCard mc = new MagicCard();
//		
//			mc.setNumber("4");
//		pricer.getPrice(ed, mc);
//	}
	
	
	public String toString()
	{
		return getName();
	}
	
	public static String getMGVILLCodeEdition(MagicEdition me)
	{
		switch (me.getId())
		{
		case "M10" :return "10m";
		case "M11" :return "11m";
		case "M12" :return "12m";
		case "M13" : return "13m";
		case "M14" : return "14m";
		case "M15" : return "15m";
		case "DRK" : return "dar";
		case "5DN" : return "fda";
		case "10E" : return "xth";
		default : return me.getId();
		}
		
		
	}
	
	private static String prefixZeros(String value, int len) {
	    char[] t = new char[len];
	    int l = value.length();
	    int k = len-l;
	    for(int i=0;i<k;i++) { t[i]='0'; }
	    value.getChars(0, l, t, k);
	    return new String(t);
	}
	
	
	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
		
		list.clear();
		String html = props.getProperty("URL");
		
		if(me==null)
			me = card.getEditions().get(0);

		
		String keyword = getMGVILLCodeEdition(me)+prefixZeros(card.getNumber(),3);
		props.put("KEYWORD", keyword);
		String url = html+keyword;
		

		logger.debug(getName() +" looking for prices " + url );
		
		doc = Jsoup.connect(url).get();
		
		Element table = doc.select("table[width=98%]").get(2); //select the first table.
		 
		 Elements rows = table.select("tr");
		 
		 for (int i = 3; i < rows.size(); i=i+2) {
			 Element ligne = rows.get(i);
			 Elements cols = ligne.getElementsByTag("td");
			 MagicPrice mp =new MagicPrice();
			 
			 String price = cols.get(4).text();
			 		price=price.substring(0, price.length()-1);
			 mp.setValue(Double.parseDouble(price));
			 mp.setCurrency("EUR");
			 mp.setSeller(cols.get(0).text());
			 mp.setSite(getName());
			 mp.setUrl(url);
			 list.add(mp);
			 
		 }
		 
		 logger.debug(getName() +" found " + list.size() +" item(s) return " + props.get("MAX") + " items" );
		
		 
		 if(list.size()>Integer.parseInt(props.get("MAX").toString()))
			 if(Integer.parseInt(props.get("MAX").toString())>-1)
				 return list.subList(0, Integer.parseInt(props.get("MAX").toString()));
		 
			 
		return list;
	}
	
	

	@Override
	public String getName() {
		return "Magic-Villois";
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

	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public void enable(boolean t) {
		this.enable=t;
		
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		return this.hashCode()==obj.hashCode();
	}
	
}



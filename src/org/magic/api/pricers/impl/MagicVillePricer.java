package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
		case "DRK" : return "dar";
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

		
		String keyword = getMGVILLCodeEdition(me)+prefixZeros(me.getNumber(),3);
		props.put("KEYWORD", keyword);
		String url = html+keyword;
		

		logger.debug(getName() +" looking for prices " + url );
		
		doc = Jsoup.connect(url).get();
		
		
		if( doc.select("table[width=95%]").size()<=0)
			return list;
		
		
		 Element table = doc.select("table[width=95%]").get(0); //select the first table.
		 Elements rows = table.select("tr");
	
		 for (int i = 3; i < rows.size(); i=i+2) {
			 Element ligne = rows.get(i);
			 Elements cols = ligne.getElementsByTag("td");
			 MagicPrice mp =new MagicPrice();
			 
			 String price = cols.get(4).text();
			 		price=price.substring(0, price.length()-1);
			 mp.setValue(Double.parseDouble(price));
			 mp.setCurrency("�");
			 mp.setSeller(cols.get(0).text());
			 mp.setSite(getName());
			 mp.setUrl(url);
			 list.add(mp);
			 
		 }
		
		 logger.debug(getName() +" found " + list.size() +" item(s) . Return " + props.getProperty("MAX")+" results");
			 
		 
		if(((int)props.get("MAX"))!=-1)
			 return list.subList(0, (int)props.get("MAX"));
		 
			 
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

}



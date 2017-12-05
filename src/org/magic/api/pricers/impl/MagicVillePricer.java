package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.MTGLogger;

public class MagicVillePricer extends AbstractMagicPricesProvider {

	Document doc;
	List<MagicPrice> list;
	CloseableHttpClient httpclient;
	
	static final Logger logger = MTGLogger.getLogger(MagicVillePricer.class);

	public MagicVillePricer() {
		super();
		
		list=new ArrayList<MagicPrice>();
		httpclient = HttpClients.createDefault();
		
		if(!new File(confdir, getName()+".conf").exists()){
				props.put("MAX", "5");
				props.put("URL", "http://www.magic-ville.com/fr/register/show_card_sale.php?gamerid=");
				props.put("WEBSITE", "http://www.magic-ville.com/");
				props.put("KEYWORD", "");	
				props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
				save();
		}
		
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
		case "CSP" : return "col";
		case "ARB" : return "alr";
		case "DST" : return "drs";
		case "5ED" : return "5th";
		case "ALA" : return "soa";
		case "TMP" : return "tem";
		case "CN2" : return "2cn";
		case "MM3" : return "mmc";
		default : return me.getId();
		}
		
		
	}
	
	private static String prefixZeros(String value, int len) {
		logger.debug("parsingNumber " + value + " "+  len) ;
	    char[] t = new char[len];
	    int l = value.trim().length();
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

		String keyword ="";
		try{
		keyword = getMGVILLCodeEdition(me)+prefixZeros(card.getNumber().replaceAll("a", "").trim(),3);
		}
		catch(Exception e)
		{
			logger.error("no number for " + card);
			return list;
		}
		props.put("KEYWORD", keyword);
		String url = html+keyword;
		

		logger.info(getName() +" looking for prices " + url );
		
		doc = Jsoup.connect(url).userAgent(props.getProperty("USER_AGENT")).timeout(0).get();
		Element table =null;
		try{
		table = doc.select("table[width=98%]").get(2); //select the first table.
		}catch(IndexOutOfBoundsException e)
		{
			logger.info(getName() +" no sellers");
			return list;
		}
		 
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
			 mp.setQuality(cols.get(2).text());
			 mp.setLanguage(cols.get(1).getElementsByTag("span").text());
			
			 
			 list.add(mp);
			 
		 }
		 
		 logger.info(getName() +" found " + list.size() +" item(s) return " + props.get("MAX") + " items" );
		
		 
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
	public void alertDetected(List<MagicPrice> p) {
		// TODO Auto-generated method stub
		
	}
	
}



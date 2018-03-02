package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.JaccardDistance;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.InstallCert;

public class CardKingdomPricer extends AbstractMagicPricesProvider {

	Document doc;
	List<MagicPrice> list;
	CloseableHttpClient httpclient;
	List<String> eds;
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	

	public CardKingdomPricer() {
		super();
		
		list=new ArrayList<>();
		httpclient = HttpClients.createDefault();
	
		if(!new File(confdir, getName()+".conf").exists()){
				props.put("URL", "https://www.cardkingdom.com/mtg/");
				props.put("WEBSITE", "https://www.cardkingdom.com/");
				props.put("USER_AGENT", MTGConstants.USER_AGENT);
				save();
		}
		
		try {
  			InstallCert.install("www.cardkingdom.com");
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGConstants.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
    	} catch (Exception e1) {
			logger.error(e1);
		}
		
		eds=new ArrayList<>();
		try {
			doc = Jsoup.connect("http://www.cardkingdom.com/catalog/magic_the_gathering/by_az").userAgent(getProperty("USER_AGENT")).timeout(0).get();
			
			Elements e = doc.select(".anchorList a[href]");
			
			for(Element ed : e)
				eds.add(ed.html());
		} catch (IOException e) {
			logger.error("Could not init list eds",e);
		}
		
		
	}
	


	private String findGoodEds(String set) {
		double leven=100;
		String name="";
		EditDistance<Double> d = new JaccardDistance();
		//JaroWinklerDistance d = new JaroWinklerDistance(); //(plus proche de 1).
		for(String s : eds)
		{
			double dist=d.apply(set.toLowerCase(), s.toLowerCase());
			logger.trace(s +" leven=" + dist + "(save="+leven+")");
			if(dist<leven)
			{
				leven=dist;
				name=s;
			}
		}
		return name;
	}
	
	
	
	public String format(String s)
	{
		return s.replaceAll("'s", "s").replaceAll(",","").replaceAll(" ", "-").toLowerCase();
	}
	
	
	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
		
		list.clear();
		String html = getProperty("URL");
		
		if(me==null)
			me = card.getEditions().get(0);

		
		String url = html+format(findGoodEds(me.getSet()))+"/"+format(card.getName());
		Elements prices =null;
		Elements qualities = null;
		

		logger.info(getName() +" looking for prices " + url );
		try{
			doc = Jsoup.connect(url).userAgent(getProperty("USER_AGENT")).timeout(0).get();
			qualities = doc.select(".cardTypeList li");
			prices = doc.select(".stylePrice");
		
		}
		catch(Exception e)
		{
			logger.info(getName() +" no item : "+ e.getMessage());
			return list;
		}
		
		List<MagicPrice> lstPrices = new ArrayList<>();
		for(int i=0;i<qualities.size();i++)
		{
			 MagicPrice mp =new MagicPrice();
			 
			 String price = prices.get(i).html().replaceAll("\\$", "");
			 mp.setValue(Double.parseDouble(price));
			 mp.setCurrency("$");
			 mp.setSeller("Card Kingdom");
			 mp.setSite(getName());
			 mp.setUrl(url);
			 mp.setQuality(qualities.get(i).html());
			 mp.setLanguage("English");
			 
			 if(!qualities.get(i).hasClass("disabled"))
			 lstPrices.add(mp);
		}
		logger.info(getName() +" found " + lstPrices.size() +" item(s)" );
		return lstPrices;
	}
	
	

	@Override
	public String getName() {
		return "Card Kingdom";
	}


	@Override
	public void alertDetected(List<MagicPrice> p) {
		logger.error("not implemented");
		
	}
	
}



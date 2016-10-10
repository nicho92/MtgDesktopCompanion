package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EbayPricer extends AbstractMagicPricesProvider
{

	String KEYWORD="";
	static final Logger logger = LogManager.getLogger(EbayPricer.class.getName());
	
	
	public EbayPricer() {
		super();	
		
		if(!new File(confdir, getName()+".conf").exists()){
		props.put("MAX", "10");
		props.put("COUNTRY", "EBAY-FR");
		props.put("API_KEY", "none04674-8d13-4421-af9e-ec641c7ee59");
		props.put("URL", "http://svcs.ebay.fr/services/search/FindingService/v1?SECURITY-APPNAME=%API_KEY%&OPERATION-NAME=findItemsByKeywords&RESPONSE-DATA-FORMAT=JSON&GLOBAL-ID=%COUNTRY%&keywords=%KEYWORD%&paginationInput.entriesPerPage=%MAX%");
		props.put("WEBSITE", "http://www.ebay.com/");
		props.put("ENCODING", "UTF-8");
		props.put("KEYWORD", "");
		save();
		}
	}
	
	
	
	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
		List<MagicPrice> prices = new ArrayList<MagicPrice>();
		
		String url = props.getProperty("URL");
			   url = url.replaceAll("%API_KEY%", props.get("API_KEY").toString());
			   url = url.replaceAll("%COUNTRY%", props.get("COUNTRY").toString());
			   url = url.replaceAll("%MAX%", props.get("MAX").toString());
		KEYWORD=card.getName();
		
		if(me!=null)
			KEYWORD += " " + me.getSet();
		
		props.put("KEYWORD", KEYWORD);
		
		KEYWORD=URLEncoder.encode(KEYWORD,props.getProperty("ENCODING"));
		
		
		
		String link=url.replaceAll("%KEYWORD%", KEYWORD);
		
		logger.info(getName() + " looking for " + KEYWORD);
		
		JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), "UTF-8"));
		JsonElement root = new JsonParser().parse(reader);
			
		JsonElement articles=root.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray().get(0).getAsJsonObject().get("searchResult");
		
		if(articles.getAsJsonArray().get(0).getAsJsonObject().get("item")==null)
		{
			logger.info(getName() + " find nothing");
			return prices;
		}
		
		
			JsonArray items = articles.getAsJsonArray().get(0).getAsJsonObject().get("item").getAsJsonArray();

			
			
			
		 	
		 	
		 	for(JsonElement el : items)
		 	{
		 		MagicPrice mp = new MagicPrice();
		 		String etat="";
		 		String title = el.getAsJsonObject().get("title").getAsString();
		 		String consultURL = el.getAsJsonObject().get("viewItemURL").getAsString();
		 		double price = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject().get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("__value__").getAsDouble();
		 		String currency = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject().get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("@currencyId").getAsString();
		 		try
		 		{
		 			etat = el.getAsJsonObject().get("condition").getAsJsonArray().get(0).getAsJsonObject().get("conditionDisplayName").getAsString();
		 		}
		 		catch(NullPointerException e)
		 		{
		 			etat="";
		 		}
		 		
		 		mp.setSeller(title);
		 		mp.setUrl(consultURL);
		 		mp.setCurrency(currency);
		 		mp.setValue(price);
		 		mp.setSite(getName());
		 		mp.setQuality(etat);
		 		prices.add(mp);
		 	}
		 	
		 	logger.info(getName() + " find " + prices.size() + " item(s)");
		 	
		java.util.Collections.sort(prices);
		return prices;
	}

	@Override
	public String getName() {
		return "Ebay";
	}

}

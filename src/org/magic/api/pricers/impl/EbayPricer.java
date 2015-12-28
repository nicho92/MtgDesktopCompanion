package org.magic.api.pricers.impl;

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
import org.magic.api.interfaces.MagicPricesProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EbayPricer implements MagicPricesProvider{

	String API_KEY="none04674-8d13-4421-af9e-ec641c7ee59";
	String KEYWORD="";
	int MAXRESULT=5;
	String COUNTRY="EBAY-FR";
	String url = "http://svcs.ebay.fr/services/search/FindingService/v1?SECURITY-APPNAME="+API_KEY+"&OPERATION-NAME=findItemsByKeywords&RESPONSE-DATA-FORMAT=JSON&GLOBAL-ID="+COUNTRY+"&keywords=%KEYWORD%&paginationInput.entriesPerPage="+MAXRESULT;
	static final Logger logger = LogManager.getLogger(EbayPricer.class.getName());

	
	public String toString()
	{
		return getName();
	}
	public List<MagicPrice> getPrice(MagicEdition me,MagicCard card) throws IOException {
		List<MagicPrice> prices = new ArrayList<MagicPrice>();
		
		
		KEYWORD=card.getName();
		
		if(me!=null)
			KEYWORD += " " + me.getSet();
		
		KEYWORD=URLEncoder.encode(KEYWORD,"UTF-8");
			
		String link=url.replaceAll("%KEYWORD%", KEYWORD);
		
		
		JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), "UTF-8"));
		JsonElement root = new JsonParser().parse(reader);
			
		JsonElement articles=root.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray().get(0).getAsJsonObject().get("searchResult");
		
		if(articles.getAsJsonArray().get(0).getAsJsonObject().get("item")==null)
			return prices;
		
		
		 	JsonArray items = articles.getAsJsonArray().get(0).getAsJsonObject().get("item").getAsJsonArray();
		 	
		 	
		 	
		 	for(JsonElement el : items)
		 	{
		 		MagicPrice mp = new MagicPrice();
		 		String title = el.getAsJsonObject().get("title").getAsString();
		 		String consultURL = el.getAsJsonObject().get("viewItemURL").getAsString();
		 		double price = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject().get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("__value__").getAsDouble();
		 		String currency = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject().get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("@currencyId").getAsString();
		 		
		 		mp.setSeller(title);
		 		mp.setUrl(consultURL);
		 		mp.setCurrency(currency);
		 		mp.setValue(price);
		 		mp.setSite(getName());
		 		prices.add(mp);
		 	}
		 	
		 	
		java.util.Collections.sort(prices);
		return prices;
	}

	@Override
	public String getName() {
		return "Ebay";
	}

	@Override
	public void setMaxResults(int max) {
		MAXRESULT=max;
		url = "http://svcs.ebay.fr/services/search/FindingService/v1?SECURITY-APPNAME="+API_KEY+"&OPERATION-NAME=findItemsByKeywords&RESPONSE-DATA-FORMAT=JSON&GLOBAL-ID="+COUNTRY+"&keywords=%KEYWORD%&paginationInput.entriesPerPage="+MAXRESULT;
		
		
	}

}

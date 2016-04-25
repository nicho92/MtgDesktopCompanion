package org.magic.api.shopping.impl;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.AbstractMagicShopper;
import org.magic.api.pricers.impl.EbayPricer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EbayShopper extends AbstractMagicShopper {

	static final Logger logger = LogManager.getLogger(EbayShopper.class.getName());

	EbayPricer pricer;
	
	public EbayShopper() {
		super();
		pricer= new EbayPricer();
		props=pricer.getProperties();
	
	}
	
	
	public List<ShopItem> search(String search) {
		List<ShopItem> prices = new ArrayList<ShopItem>();
		
	try{	
		String url = props.getProperty("URL");
		   url = url.replaceAll("%API_KEY%", props.get("API_KEY").toString());
		   url = url.replaceAll("%COUNTRY%", props.get("COUNTRY").toString());
		   url = url.replaceAll("%MAX%", props.get("MAX").toString());
	
	String KEYWORD=URLEncoder.encode(search,props.getProperty("ENCODING"));
	
	
	
	String link=url.replaceAll("%KEYWORD%", KEYWORD);
	
	logger.info(getShopName() + " looking for " + link);
	
	JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), "UTF-8"));
	JsonElement root = new JsonParser().parse(reader);
		
	JsonElement articles=root.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray().get(0).getAsJsonObject().get("searchResult");
	
	logger.debug(articles);
	
	
	if(articles.getAsJsonArray().get(0).getAsJsonObject().get("item")==null)
		return prices;
	
	
	 	JsonArray items = articles.getAsJsonArray().get(0).getAsJsonObject().get("item").getAsJsonArray();
	 	
	 	
	 	
	 	for(JsonElement el : items)
	 	{
	 		ShopItem mp = new ShopItem();
	 		
	 		String title = el.getAsJsonObject().get("title").getAsString();
	 		String type = el.getAsJsonObject().get("primaryCategory").getAsJsonArray().get(0).getAsJsonObject().get("categoryName").getAsString();
	 		String consultURL = el.getAsJsonObject().get("viewItemURL").getAsString();
	 		double price = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject().get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("__value__").getAsDouble();
	 		URL image =new URL(el.getAsJsonObject().get("galleryURL").getAsJsonArray().get(0).getAsString());
	 		Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(el.getAsJsonObject().get("listingInfo").getAsJsonArray().get(0).getAsJsonObject().get("startTime").getAsString());
	 		String id =  el.getAsJsonObject().get("itemId").getAsString();
	 		mp.setName(title);
	 		mp.setUrl(new URL(consultURL));
	 		mp.setPrice(price);
	 		mp.setType(type);
	 		mp.setShopName(getShopName());
	 		mp.setImage(image);
	 		mp.setDate(d);
	 		mp.setId(id);
	 		prices.add(mp);
	 	}
	 	
	}
	catch(Exception e)
	{
		logger.error(e);
	}
	return prices;
	}

	@Override
	public String getShopName() {
		return "Ebay";
	}

}

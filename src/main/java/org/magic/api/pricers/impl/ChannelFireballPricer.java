package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChannelFireballPricer extends AbstractPricesProvider {

	private static final String BASEURL="https://shop.channelfireball.com";
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		ArrayList<MagicPrice> list = new ArrayList<>();
		JsonObject el = URLTools.extractJson(BASEURL+"/search/suggest.json?q="+URLTools.encode(card.getName())+"&resources%5Btype%5D=product&resources%5Blimit%5D=10&resources%5Boptions%5D%5Bfields%5D=title").getAsJsonObject();
		JsonArray products = el.get("resources").getAsJsonObject().get("results").getAsJsonObject().get("products").getAsJsonArray();
		
		
		for(JsonElement product : products)
		{
			JsonObject obj = product.getAsJsonObject();
			
			if(obj.get("available").getAsBoolean() && obj.get("type").getAsString().equals("MTG Single"))
			{
				String docPage = URLTools.extractAsString(BASEURL+obj.get("url").getAsString());
				String startTag = "product: ";
				docPage = docPage.substring(docPage.indexOf(startTag)+startTag.length());
				String endTag = "\n";
				docPage = docPage.substring(0,docPage.indexOf(endTag)-1);
			
				JsonArray arrArticles = URLTools.toJson(docPage).getAsJsonObject().get("variants").getAsJsonArray();
				for(JsonElement article : arrArticles)
				{
					JsonObject art = article.getAsJsonObject();
					
					if(art.get("available").getAsBoolean())
					{
						MagicPrice mp = new MagicPrice();
								   mp.setSite(getName());
								   mp.setUrl(BASEURL+obj.get("url").getAsString());
								   mp.setCurrency("USD");
								   mp.setCountry("USA");
								   mp.setQuality(art.get("title").getAsString().replace("Foil", "").trim());
								   mp.setMagicCard(card);
								   mp.setFoil(obj.get("title").getAsString().toLowerCase().contains("foil"));
								   mp.setValue(art.get("price").getAsDouble()/100);
								   mp.setLanguage("English");
								   
								   
								   String set = art.get("name").getAsString().substring(art.get("name").getAsString().indexOf("["));
								   mp.setSeller(set.substring(1,set.indexOf("]")));
								   
								   boolean showcase = art.get("name").getAsString().toLowerCase().contains("(showcase)");
								   boolean borderless = art.get("name").getAsString().toLowerCase().contains("(borderless)");
								   boolean extended = art.get("name").getAsString().toLowerCase().contains("(extended art)");
								   
								   //TODO filter for card extra properties
								   
								   list.add(mp);
								   
					}
				}
				
				
			}
		}
		logger.info(getName() + " found " + list.size() + " item(s)");
		return list;
	}

	@Override
	public String getName() {
		return "Channel Fireball";
	}
	
	@Override
	public String getVersion() {
		return "2.0";
	}

}

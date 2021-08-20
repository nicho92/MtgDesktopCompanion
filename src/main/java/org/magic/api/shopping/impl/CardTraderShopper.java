package org.magic.api.shopping.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CardTraderShopper extends AbstractMagicShopper {

	private static final String TOKEN = "TOKEN";
	private static final String BASE="https://api.cardtrader.com/api/full/v1";
	
	private URLToolsClient client;
	
	
	public CardTraderShopper() {
		client = URLTools.newClient();
	}
	
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		var bearer = "Bearer "+getString(TOKEN);
		List<OrderEntry> orders = new ArrayList<>();
		var arr = RequestBuilder.build().url(BASE+"/orders")
					.method(METHOD.GET)
					.setClient(client)
					.addHeader("Authorization", bearer)
					.addContent("sort","date.desc").toJson();
		
	
		for(JsonElement o : arr.getAsJsonArray())
		{
			orders.addAll(parseOrder(RequestBuilder.build().url(BASE+"/orders/"+o.getAsString())
					.method(METHOD.GET)
					.setClient(client)
					.addHeader("Authorization", bearer).toJson().getAsJsonObject()));
		}
		return orders;
	}
	
	
	private List<OrderEntry> parseOrder(JsonObject obj) {
		List<OrderEntry> items = new ArrayList<>();
		
		
		for(JsonElement e :obj.get("order_items").getAsJsonArray())
		{
			var entry = e.getAsJsonObject();
			
			var ord = new OrderEntry();
				   ord.setIdTransation(entry.get("id").getAsString());
				   ord.setCurrency(Currency.getInstance(entry.get("price_currency").getAsString()));
				   ord.setSource(getName());
				   ord.setDescription(entry.get("name").getAsString());
				   ord.setItemPrice((double)entry.get("price_cents").getAsInt()/100);
				   ord.setTypeTransaction(TransactionDirection.BUY);
				   ord.setType(parseType(entry.get("category_id").getAsInt()));
				   try {
					ord.setEdition(getEnabledPlugin(MTGCardsProvider.class).getSetByName(entry.get("expansion").getAsString()));
				} catch (IOException e1) {
					logger.error("no set found for " + entry.get("expansion"));
				}
			items.add(ord);	   
				   
				   
				   
		}		   
		return items;
	}



	private EnumItems parseType(int idC) {
		switch (idC) {
		case 1 : return EnumItems.CARD;
		case 4: return EnumItems.BOX;
		case 5 : return EnumItems.BOOSTER;
		case 6 : return EnumItems.FULLSET;
		case 7 : return EnumItems.STARTER;
		case 17 : return EnumItems.CONSTRUCTPACK;
		case 23 : return EnumItems.BUNDLE;
		default: return null;
		}
	}


	@Override
	public String getName() {
		return "CardTrader";
	}

	

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(TOKEN, "");
	}

}

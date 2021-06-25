package org.magic.api.shopping.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.Transaction.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.api.interfaces.abstracts.AbstractStockItem.TYPESTOCK;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CardTraderShopper extends AbstractMagicShopper {

	private static final String TOKEN = "TOKEN";
	private static final String BASE="https://www.cardtrader.com/api/simple/v1/";
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		
		List<OrderEntry> orders = new ArrayList<>();
		var arr = URLTools.extractJson(BASE+"/orders?token="+getString(TOKEN)).getAsJsonArray();
		
		for(JsonElement o : arr)
		{
			orders.addAll(parseOrder(URLTools.extractJson(BASE+"/orders/"+o.getAsString()+"?token="+getString(TOKEN)).getAsJsonObject()));
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
				   ord.setTypeTransaction(TYPE_TRANSACTION.BUY);
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



	private TYPESTOCK parseType(int idC) {
		switch (idC) {
		case 1 : return TYPESTOCK.CARD;
		case 2 : return TYPESTOCK.BOOSTER;
		default: return null;
		}
	}


	@Override
	public String getName() {
		return "CardTrader";
	}

	

	@Override
	public void initDefault() {
		setProperty(TOKEN, "");
	}

}

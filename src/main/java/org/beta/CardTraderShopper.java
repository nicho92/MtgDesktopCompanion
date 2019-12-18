package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.OrderEntry;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class CardTraderShopper extends AbstractMagicShopper {

	private static final String TOKEN = "TOKEN";
	private static final String BASE="https://www.cardtrader.com/api/simple/v1/";
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		
		
		List<OrderEntry> orders = new ArrayList<>();
		JsonArray arr = URLTools.extractJson(BASE+"/orders?token="+getString(TOKEN)).getAsJsonArray();
		
		for(JsonElement o : arr)
		{
			OrderEntry oe = new OrderEntry();
			URLTools.extractJson(BASE+"/orders/"+o.getAsString()+"token="+getString(TOKEN)).getAsJsonObject();
			
			
			orders.add(oe);
		}
		
		
		return orders;
	}
	
	
	public static void main(String[] args) throws IOException {
		new CardTraderShopper().listOrders();
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

package org.magic.api.shopping.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.WooCommerceTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceShopper extends AbstractMagicShopper{

	private static final String WEBSITE = "WEBSITE";
	private static final String CONSUMER_KEY = "CONSUMER_KEY";
	private static final String CONSUMER_SECRET = "CONSUMER_SECRET";
	private WooCommerce wooCommerce;

	@Override
	public void initDefault() {
		setProperty(WEBSITE, "https://mysite.fr/");
		setProperty(CONSUMER_KEY, "");
		setProperty(CONSUMER_SECRET, "");
	}
	

	@Override
	public String getName() {
		return "WooCommerce";
	}
	
	private void init()
	{
		wooCommerce = WooCommerceTools.build(getString(CONSUMER_KEY), getString(CONSUMER_SECRET),getString(WEBSITE),getVersion());
	}
	
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		
		if(wooCommerce==null)
			init();
		
		
		List<OrderEntry> list = new ArrayList<>();
		
		Map<String, String> parameters = new HashMap<>();
						    parameters.put("per_page", "100");
						    parameters.put("status", "completed");
						    
		List<JsonElement> ret = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(),parameters);
		System.out.println(ret);
		
		
		for(JsonElement el : ret)
		{
			JsonObject obj = el.getAsJsonObject();
			
			for(JsonElement item : obj.get("line_items").getAsJsonArray())
			{
				JsonObject itemObj = item.getAsJsonObject();
				var oe = new OrderEntry();
				   oe.setCurrency(obj.get("currency").getAsString());
				   oe.setIdTransation(obj.get("id").getAsString());
				   oe.setTransactionDate(WooCommerceTools.toDate(obj.get("date_completed").getAsString()));
				   oe.setDescription(itemObj.get("name").getAsString());
				   oe.setSeller(getString(WEBSITE));
				   oe.setSource(getString(WEBSITE));
				   oe.setItemPrice(itemObj.get("price").getAsDouble());
				   oe.setTypeTransaction(TYPE_TRANSACTION.BUY);
				   list.add(oe);
			}
		}
		return list;
	}

	@Override
	public String getVersion() {
		return "V3";
	}
	

}

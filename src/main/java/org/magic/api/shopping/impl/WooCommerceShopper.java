package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.UITools;
import org.magic.tools.WooCommerceTools;

import com.google.gson.JsonElement;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

public class WooCommerceShopper extends AbstractMagicShopper{

	private static final String PER_PAGE = "PER_PAGE";
	private static final String STATUS = "STATUS";
	private WooCommerce wooCommerce;


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(STATUS,"any",
							   PER_PAGE,"100");
	}



	@Override
	public String getName() {
		return "WooCommerce";
	}

	private void init()
	{
		wooCommerce = WooCommerceTools.newClient(getAuthenticator(),getVersion());
	}


	@Override
	public List<OrderEntry> listOrders() throws IOException {

		if(wooCommerce==null)
			init();


		List<OrderEntry> list = new ArrayList<>();

		Map<String, String> parameters = new HashMap<>();
						    parameters.put("per_page", getString(PER_PAGE));
						    parameters.put("status", getString(STATUS));

		@SuppressWarnings("unchecked")
		List<JsonElement> ret = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(),parameters);
		for(JsonElement el : ret)
		{
			var obj = el.getAsJsonObject();

			for(JsonElement item : obj.get("line_items").getAsJsonArray())
			{
				var itemObj = item.getAsJsonObject();
				var oe = new OrderEntry();
				   oe.setCurrency(obj.get("currency").getAsString());
				   oe.setIdTransation(obj.get("id").getAsString());
				   oe.setTransactionDate(UITools.parseGMTDate(obj.get("date_created").getAsString()));
				   oe.setDescription(itemObj.get("name").getAsString());
				   oe.setSeller(obj.get("billing").getAsJsonObject().get("last_name").getAsString());
				   oe.setSource(getString(getAuthenticator().get("WEBSITE")));
				   oe.setItemPrice(itemObj.get("price").getAsDouble());
				   oe.setTypeTransaction(TransactionDirection.BUY);
				   oe.setShippingPrice(obj.get("shipping_total").getAsDouble());
				   list.add(oe);
			}
		}
		return list;
	}

	@Override
	public String getVersion() {
		return "V3";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return WooCommerceTools.generateKeysForWooCommerce();
	}


}

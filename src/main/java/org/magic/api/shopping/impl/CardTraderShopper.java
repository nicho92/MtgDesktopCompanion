package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardtrader.services.CardTraderService;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;

public class CardTraderShopper extends AbstractMagicShopper {

	private static final String TOKEN = "TOKEN";
	
	public CardTraderShopper() {
	
	}
	
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		List<OrderEntry> orders = new ArrayList<>();
		
		new CardTraderService(getAuthenticator().get(TOKEN)).listOrders(1).forEach(o->{
		   var ord = new OrderEntry();
		   ord.setIdTransation(""+o.getId());
		   ord.setSource(getName());
		   ord.setTypeTransaction(TransactionDirection.BUY);
      	   orders.add(ord);
		});
		return orders;
	}

	@Override
	public String getName() {
		return "CardTrader";
	}

	
	@Override
	public List<String> listAuthenticationAttributes() {
			return List.of(TOKEN);
	}

}

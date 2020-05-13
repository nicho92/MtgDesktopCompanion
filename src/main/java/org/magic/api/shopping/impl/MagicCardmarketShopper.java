package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.api.mkm.exceptions.MkmException;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Order;
import org.api.mkm.services.OrderService;
import org.api.mkm.services.OrderService.ACTOR;
import org.api.mkm.services.OrderService.STATE;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.services.MTGControler;

public class MagicCardmarketShopper extends AbstractMagicShopper {

	private boolean initied=false;

	private void init()
	{
		try {
			MkmAPIConfig.getInstance().init(new MagicCardMarketPricer2().getProperties());
			initied=true;
		} catch (MkmException e) {
			logger.error(e);
		}
	}
	
	
	private OrderEntry toOrder(LightArticle a, Order o,TYPE_TRANSACTION t)
	{
		OrderEntry entrie = new OrderEntry();
		entrie.setIdTransation(""+o.getIdOrder());
		entrie.setCurrency(Currency.getInstance("EUR"));
		entrie.setTransactionDate(o.getState().getDatePaid());
		entrie.setTypeTransaction(t);
		entrie.setSeller(o.getSeller().getUsername());
		entrie.setShippingPrice(o.getTotalValue()-o.getArticleValue());
		entrie.setSource(getName());
		entrie.setItemPrice(a.getPrice());
		entrie.setDescription(a.getProduct().getEnName());
		
		if(a.getProduct().getExpansion()!=null)
		{
			entrie.setType(TYPE_ITEM.CARD);
			
			try {
				entrie.setEdition(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetByName(a.getProduct().getExpansion()));
			} catch (IOException e) {
				logger.error("can't found " + a.getProduct().getExpansion());
			}
		}
		else if(a.getProduct().getEnName().toLowerCase().contains("booster box"))
		{
			entrie.setType(TYPE_ITEM.BOX);
		}
		
		return entrie;
	}
	
	@Override
	public List<OrderEntry> listOrders() throws IOException
	{
		if(!initied)
			init();
		
		List<OrderEntry> entries = new ArrayList<>();
		OrderService serv = new OrderService();
		
		serv.listOrders(ACTOR.buyer, STATE.bought, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TYPE_TRANSACTION.BUY))));
		serv.listOrders(ACTOR.buyer, STATE.received, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TYPE_TRANSACTION.BUY))));
		serv.listOrders(ACTOR.buyer, STATE.paid, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TYPE_TRANSACTION.BUY))));
		serv.listOrders(ACTOR.seller, STATE.received, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TYPE_TRANSACTION.SELL))));
		serv.listOrders(ACTOR.seller, STATE.sent, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TYPE_TRANSACTION.SELL))));
	
		return entries;
	}

	@Override
	public String getName() {
		return "MagicCardMarket";
	}
	
}

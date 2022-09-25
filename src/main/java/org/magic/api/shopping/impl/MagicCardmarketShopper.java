package org.magic.api.shopping.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

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
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;

public class MagicCardmarketShopper extends AbstractMagicShopper {

	private boolean initied=false;

	private void init()
	{
		try {
			MkmAPIConfig.getInstance().init(getAuthenticator().getTokensAsProperties());
			initied=true;
		} catch (MkmException e) {
			logger.error(e);
		}
	}


	private OrderEntry toOrder(LightArticle a, Order o,TransactionDirection t)
	{
		var entrie = new OrderEntry();
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
			entrie.setType(EnumItems.CARD);

			try {
				entrie.setEdition(getEnabledPlugin(MTGCardsProvider.class).getSetByName(a.getProduct().getExpansion()));
			} catch (IOException e) {
				logger.error("can't found {}",a.getProduct().getExpansion());
			}
		}
		else if(a.getProduct().getEnName().toLowerCase().contains("booster box"))
		{
			entrie.setType(EnumItems.BOX);
		}

		return entrie;
	}

	@Override
	public List<OrderEntry> listOrders() throws IOException
	{
		if(!initied)
			init();

		List<OrderEntry> entries = new ArrayList<>();
		var serv = new OrderService();

		serv.listOrders(ACTOR.buyer, STATE.bought, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TransactionDirection.BUY))));
		serv.listOrders(ACTOR.buyer, STATE.received, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TransactionDirection.BUY))));
		serv.listOrders(ACTOR.buyer, STATE.paid, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TransactionDirection.BUY))));
		serv.listOrders(ACTOR.seller, STATE.received, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TransactionDirection.SELL))));
		serv.listOrders(ACTOR.seller, STATE.sent, null).forEach(o->o.getArticle().forEach(a->entries.add(toOrder(a, o,TransactionDirection.SELL))));

		return entries;
	}

	@Override
	public String getName() {
		return MkmConstants.MKM_NAME;
	}

	@Override
	public String getVersion() {
		return MkmConstants.MKM_API_VERSION;
	}

}

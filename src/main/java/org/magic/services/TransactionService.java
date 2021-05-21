package org.magic.services;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.notifiers.impl.EmailNotifier;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionService {
	protected static Logger logger = MTGLogger.getLogger(TransactionService.class);

	public static void update(Transaction t) throws SQLException {
		
		try {
			var js = new JavaScript();
			js.addVariable("total", t.getTotal());
			Object ret = js.runContent(MTGControler.getInstance().getWebConfig().getShippingRules());
			t.setShippingPrice(Double.parseDouble(ret.toString()));
		} catch (Exception e1) {
			logger.error("Error updating shipping price",e1);
		}
		getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
			
		
	}

	public static OrderEntry toOrder(Transaction t,MagicCardStock transactionItem)
	{
		 
		   var oe = new OrderEntry();
			   oe.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			   oe.setDescription(transactionItem.getMagicCard().getName());
			   oe.setEdition(transactionItem.getMagicCard().getCurrentSet());
			   oe.setIdTransation(String.valueOf(t.getId()));
			   oe.setItemPrice(UITools.roundDouble(transactionItem.getPrice()));
			   oe.setTransactionDate(t.getDateProposition());
			   oe.setShippingPrice(UITools.roundDouble(t.getShippingPrice()));
			   oe.setSource(MTGControler.getInstance().getWebConfig().getSiteTitle());
			   oe.setType(TYPE_ITEM.CARD);
			   oe.setUpdated(false);
			   if(t.total()>0)								   
				   oe.setTypeTransaction(TYPE_TRANSACTION.SELL);
			   else
				   oe.setTypeTransaction(TYPE_TRANSACTION.BUY);
			   
			   return oe;
	}

	public static int saveTransaction(Transaction t) throws SQLException {
		int ret = getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);

		if(t.getContact().isEmailAccept()) 
		{
			try {
					EmailNotifier plug = (EmailNotifier)MTG.getPlugin("email", MTGNotifier.class);
					var not = new MTGNotification("["+t.getConfig().getSiteTitle()+ "] Order #"+t.getId(), new ReportNotificationManager().generate(plug.getFormat(), t, Transaction.class), MTGNotification.MESSAGE_TYPE.INFO);
					plug.send(t.getContact().getEmail(),not);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		}
		return ret;
	}
	
	
}

package org.magic.services;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.Transaction.PAYMENT_PROVIDER;
import org.magic.api.beans.Transaction.STAT;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.notifiers.impl.EmailNotifier;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionService {
	protected static Logger logger = MTGLogger.getLogger(TransactionService.class);

	public static int saveTransaction(Transaction t, boolean reloadShipping) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		if(reloadShipping) {
			try {
				var js = new JavaScript();
				js.addVariable("total", t.getTotal());
				Object ret = js.runContent(MTGControler.getInstance().getWebConfig().getShippingRules());
				t.setShippingPrice(Double.parseDouble(ret.toString()));
			} catch (Exception e1) {
				logger.error("Error updating shipping price",e1);
			}
		}
		return getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
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

	public static void sendMail(Transaction t,String template,String msg)
	{
		EmailNotifier plug = (EmailNotifier)MTG.getPlugin("email", MTGNotifier.class);
		if(t.getContact().isEmailAccept()) 
		{
			try {
					var not = new MTGNotification("["+t.getConfig().getSiteTitle()+ "] Order #"+t.getId() + ":" + msg , new ReportNotificationManager().generate(plug.getFormat(), t, template), MTGNotification.MESSAGE_TYPE.INFO);
					plug.send(t.getContact().getEmail(),not);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		}
		
	}
	
	public static Integer newTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(STAT.NEW);
		t.setCurrency(t.getConfig().getCurrency());
		int ret = saveTransaction(t,false);
		sendMail(t,"TransactionNew","Transaction received");
		
		MTGControler.getInstance().notify(new MTGNotification("New Transaction","New trnsaction from " + t.getContact(),MESSAGE_TYPE.INFO));
		return ret;
	
	}
	
	
	public static void sendTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(STAT.SENT);
		t.setDateSend(new Date());
		saveTransaction(t,false);
		sendMail(t,"TransactionSent", "Shipped !");	
	}
	
	public static void validateTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(STAT.ACCEPTED);
		saveTransaction(t,false);
		sendMail(t,"TransactionValid","your order validate !");	
	
	}
	
	public static void payingTransaction(Transaction t, String providerName) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(STAT.PAID);
		t.setPaymentProvider(PAYMENT_PROVIDER.valueOf(providerName.toUpperCase()));
		t.setDatePayment(new Date());
		saveTransaction(t,false);
	
	}
	
	
	public static void mergeTransactions(List<Transaction> ts) throws SQLException, IOException {
		
		Transaction t = ts.get(0);
		
		for(var i=1;i<ts.size();i++)
		{
			if(t.getContact() != ts.get(0).getContact())
			{
				throw new IOException("Users are differents");
			}
			
			t.getItems().addAll(ts.get(i).getItems());
			
			try {
				getEnabledPlugin(MTGDao.class).deleteTransaction(ts.get(i));
			} catch (SQLException e) {
				logger.error(e);
			}
			
		}
		
		
		saveTransaction(t,false);
			
	
	}
	
}

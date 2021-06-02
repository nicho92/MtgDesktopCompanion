package org.magic.services;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.magic.api.interfaces.MTGServer;
import org.magic.api.notifiers.impl.EmailNotifier;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionService 
{
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
		
		MTGControler.getInstance().notify(new MTGNotification("New Transaction","New transaction from " + t.getContact(),MESSAGE_TYPE.INFO));
		
		if(t.getConfig().isAutomaticValidation())
			ThreadManager.getInstance().executeThread(()->{
					try {
						validateTransaction(t);
					} catch (Exception e) {
						logger.error(e);
					}
			}, "Transaction " + t.getId() +" validation");
			
		
		return ret;
	
	}
	
	public static MagicCardStock getBestProduct() throws SQLException {
	
		Map<MagicCardStock, Integer> items = new HashMap<>() ;
		for(var t : getEnabledPlugin(MTGDao.class).listTransactions())
			for(var m : t.getItems())
				items.put(m, items.getOrDefault(m, 0)+m.getQte());

		int max = Collections.max(items.values());
		return items.entrySet().stream().filter(entry -> entry.getValue() == max).map(Entry::getKey).findAny().orElse(null);
	}
	
	public static List<MagicCardStock> validateTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		List<MagicCardStock> st = new ArrayList<>();

		for(MagicCardStock transactionItem : t.getItems())
		{
				MagicCardStock stock = getEnabledPlugin(MTGDao.class).getStockById(transactionItem.getIdstock());
				if(transactionItem.getQte()>stock.getQte())
				{
					   t.setStatut(STAT.IN_PROGRESS);
					   st.add(transactionItem);
					   transactionItem.setComment("Not enought Stock ( "+stock.getQte()+")");
				}
				else
				{
					   transactionItem.setComment("");
					   stock.setQte(stock.getQte()-transactionItem.getQte());
					   stock.setUpdate(true);
					   t.setStatut(STAT.PAYMENT_WAITING);
					   getEnabledPlugin(MTGDao.class).saveOrUpdateStock(stock);
					   getEnabledPlugin(MTGDao.class).saveOrUpdateOrderEntry(toOrder(t, transactionItem));
					 
				}
		}
		
		if(st.isEmpty())
			  sendMail(t,"TransactionValid","your order validate !");	
		
		saveTransaction(t,false);
		((JSONHttpServer)MTG.getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache();
		
		return st;
		
	}
	
	public static void cancelTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		
		for(MagicCardStock transactionItem : t.getItems())
		{
				MagicCardStock stock = getEnabledPlugin(MTGDao.class).getStockById(transactionItem.getIdstock());
					   stock.setQte(stock.getQte()+transactionItem.getQte());
					   stock.setUpdate(true);
					   t.setStatut(STAT.CANCELED);
					   getEnabledPlugin(MTGDao.class).saveOrUpdateStock(stock);
		}
		
		saveTransaction(t,false);
		((JSONHttpServer)MTG.getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache();
	}
	
	
	
	
	public static void payingTransaction(Transaction t, String providerName) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(STAT.PAID);
		t.setPaymentProvider(PAYMENT_PROVIDER.valueOf(providerName.toUpperCase()));
		t.setDatePayment(new Date());
		saveTransaction(t,false);
		sendMail(t,"TransactionPaid","Payment Accepted !");	
	
	}
	

	public static void sendTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(STAT.SENT);
		t.setDateSend(new Date());
		saveTransaction(t,false);
		sendMail(t,"TransactionSent", "Shipped !");	
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

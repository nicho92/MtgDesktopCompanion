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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.Contact;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.Transaction.TransactionDirection;
import org.magic.api.beans.Transaction.TransactionPayementProvider;
import org.magic.api.beans.Transaction.TransactionStatus;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.notifiers.impl.EmailNotifier;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionService 
{
	
	public static final Integer TOKENSIZE = 50;
	protected static Logger logger = MTGLogger.getLogger(TransactionService.class);

	private TransactionService() {}
	
	public static int createContact(Contact c) throws SQLException
	{
		c.setTemporaryToken(RandomStringUtils.random(TOKENSIZE, true, true));
		c.setActive(false);
		
		int ret= getEnabledPlugin(MTGDao.class).saveOrUpdateContact(c);
		
		c.setTemporaryToken(MTGControler.getInstance().getWebConfig().getWebsiteUrl()+"/pages/validate.html?token="+c.getTemporaryToken());
		
		EmailNotifier plug = (EmailNotifier)MTG.getPlugin(MTGConstants.EMAIL_NOTIFIER_NAME, MTGNotifier.class);
			try {
					var not = new MTGNotification("["+MTGControler.getInstance().getWebConfig().getSiteTitle()+ "] Email verification", new ReportNotificationManager().generate(plug.getFormat(), c, "ContactValidation"), MTGNotification.MESSAGE_TYPE.INFO);
					plug.send(c.getEmail(),not);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		return ret;
		
	}
	
	public static int saveTransaction(Transaction t, boolean reloadShipping) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		if(reloadShipping) {
			try {
				var js = new JavaScript();
				js.addVariable("total", t.total());
				Object ret = js.runContent(MTGControler.getInstance().getWebConfig().getShippingRules());
				t.setShippingPrice(Double.parseDouble(ret.toString()));
			} catch (Exception e1) {
				logger.error("Error updating shipping price",e1);
			}
		}
		return getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
	}
	
	

	public static OrderEntry toOrder(Transaction t,MTGStockItem transactionItem)
	{
		 
		   var oe = new OrderEntry();
			   oe.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			   oe.setDescription(transactionItem.getProductName());
			   oe.setEdition(transactionItem.getEdition());
			   oe.setIdTransation(String.valueOf(t.getId()));
			   oe.setItemPrice(UITools.roundDouble(transactionItem.getPrice()));
			   oe.setTransactionDate(t.getDateCreation());
			   oe.setShippingPrice(UITools.roundDouble(t.getShippingPrice()));
			   oe.setSource(MTGControler.getInstance().getWebConfig().getSiteTitle());
			   oe.setType(EnumItems.CARD);
			   oe.setUpdated(false);
			   if(t.total()>0)								   
				   oe.setTypeTransaction(TransactionDirection.SELL);
			   else
				   oe.setTypeTransaction(TransactionDirection.BUY);
			   
			   return oe;
	}

	public static void sendMail(Transaction t,String template,String msg)
	{
		EmailNotifier plug = (EmailNotifier)MTG.getPlugin(MTGConstants.EMAIL_NOTIFIER_NAME, MTGNotifier.class);
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
		t.setStatut(TransactionStatus.NEW);
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
		List<MagicCardStock> rejectsT = new ArrayList<>();
		List<MagicCardStock> accepteds = new ArrayList<>();
		for(MagicCardStock transactionItem : t.getItems())
		{
				MagicCardStock stock = getEnabledPlugin(MTGDao.class).getStockById(transactionItem.getId());
				if(transactionItem.getQte()>stock.getQte())
				{
					   t.setStatut(TransactionStatus.IN_PROGRESS);
					   rejectsT.add(transactionItem);
					   transactionItem.setComment("Not enought Stock ( "+stock.getQte()+"/"+transactionItem.getQte()+")");
				}
				else
				{
					   transactionItem.setComment("");
					   stock.setQte(stock.getQte()-transactionItem.getQte());
					   stock.setUpdated(true);
					   accepteds.add(stock);
				}
		}
		
		if(rejectsT.isEmpty() && !accepteds.isEmpty())
		{
			t.setStatut(TransactionStatus.PAYMENT_WAITING);
			for(MagicCardStock stock : accepteds) {
				getEnabledPlugin(MTGDao.class).saveOrUpdateStock(stock);
				getEnabledPlugin(MTGDao.class).saveOrUpdateOrderEntry(toOrder(t, stock));
			}
			sendMail(t,"TransactionValid"," your order is validate !");	
			((JSONHttpServer)MTG.getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache();
			
		}
		else
		{
			t.setStatut(TransactionStatus.IN_PROGRESS);
		}
		
		saveTransaction(t,false);
		
		return rejectsT;
		
	}
	
	public static void cancelTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		
		for(MagicCardStock transactionItem : t.getItems())
		{
				MagicCardStock stock = getEnabledPlugin(MTGDao.class).getStockById(transactionItem.getId());
					   stock.setQte(stock.getQte()+transactionItem.getQte());
					   stock.setUpdated(true);
					   t.setStatut(TransactionStatus.CANCELED);
					   getEnabledPlugin(MTGDao.class).saveOrUpdateStock(stock);
		}
		
		saveTransaction(t,false);
		((JSONHttpServer)MTG.getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache();
	}
	
	
	
	
	public static void payingTransaction(Transaction t, String providerName) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		
		if(TransactionPayementProvider.VIREMENT.equals(t.getPaymentProvider()) || TransactionPayementProvider.PAYPALME.equals(t.getPaymentProvider()))
			t.setStatut(TransactionStatus.PAYMENT_SENT);
		else
			t.setStatut(TransactionStatus.PAID);
		
		
		t.setPaymentProvider(TransactionPayementProvider.valueOf(providerName.toUpperCase()));
		t.setDatePayment(new Date());
		saveTransaction(t,false);
		sendMail(t,"TransactionPaid","Payment Accepted !");	
	
	}
	

	public static void sendTransaction(Transaction t) throws SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(TransactionStatus.SENT);
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

	
	public static boolean isWoocommerceAvailable(Transaction t) {
		for(MTGStockItem mcs : t.getItems())
		{	
			if(mcs.getTiersAppIds(new WooCommerceExport().getName())==null)
				return false;
		}
		
		return true;
	}
	
	

}

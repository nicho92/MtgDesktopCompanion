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
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionPayementProvider;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.notifiers.impl.EmailNotifier;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

public class TransactionService 
{
	
	public static final Integer TOKENSIZE = 50;
	protected static Logger logger = MTGLogger.getLogger(TransactionService.class);
	private static MTGExternalShop mtgshop=MTG.getEnabledPlugin(MTGExternalShop.class);
	private TransactionService() {	}
	
	public static int createContact(Contact c) throws IOException
	{
		
		c.setTemporaryToken(RandomStringUtils.random(TOKENSIZE, true, true));
		c.setActive(false);
		
		int ret= mtgshop.saveOrUpdateContact(c);
		
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
	
	public static Long saveTransaction(Transaction t, boolean reloadShipping) throws IOException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		if(reloadShipping) {
			try {
				var js = new JavaScript();
				js.addVariable("total", t.total());
				js.addVariable("qty",t.getItems().size());
				js.addVariable("transaction",t);
				
				Object ret = js.runContent(MTGControler.getInstance().getWebConfig().getShippingRules());
				t.setShippingPrice(Double.parseDouble(ret.toString()));
			} catch (Exception e1) {
				logger.error("Error updating shipping price",e1);
			}
		}
		
		Contact pContact = mtgshop.getContactByEmail(t.getContact().getEmail());
		
		if(pContact!=null)
		{
			logger.debug("Contact " + pContact + " found");
			t.setContact(pContact);
		}
		
		if(t.getContact().getId()<=0)
		{
			logger.debug(t.getContact() + " doesn't exist. Creating it");
			int id= createContact(t.getContact());
			t.getContact().setId(id);
		}
		
		return mtgshop.saveOrUpdateTransaction(t);
	}


	public static OrderEntry toOrder(Transaction t,MTGStockItem transactionItem)
	{
		 
		   var oe = new OrderEntry();
			   oe.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			   oe.setDescription(transactionItem.getProduct().getName());
			   oe.setEdition(transactionItem.getProduct().getEdition());
			   oe.setIdTransation(String.valueOf(t.getId()));
			   oe.setItemPrice(UITools.roundDouble(transactionItem.getPrice()));
			   oe.setTransactionDate(t.getDateCreation());
			   oe.setShippingPrice(UITools.roundDouble(t.getShippingPrice()));
			   oe.setSource(MTGControler.getInstance().getWebConfig().getSiteTitle());
			   oe.setType(transactionItem.getProduct().getTypeProduct());
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
	
	
	public static Long newTransaction(Transaction t) throws IOException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		t.setStatut(TransactionStatus.NEW);
		t.setCurrency(t.getConfig().getCurrency());
		var ret = saveTransaction(t,false);
		sendMail(t,"TransactionNew","Transaction received");
		
		MTGControler.getInstance().notify(new MTGNotification("New Transaction","New transaction from " + t.getContact(),MESSAGE_TYPE.INFO));
		
		if(t.getConfig().isAutomaticValidation())
			ThreadManager.getInstance().executeThread(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {
					try {
						validateTransaction(t);
					} catch (Exception e) {
						logger.error(e);
					}
					
				}
			}, "Transaction " + t.getId() +" validation");
			
		
		return ret;
	
	}
	
	public static MagicCardStock getBestProduct() throws SQLException {
	
		Map<MagicCardStock, Integer> items = new HashMap<>() ;
		for(var t : getEnabledPlugin(MTGDao.class).listTransactions())
			for(var m : t.getItems().stream().filter(msi->msi.getProduct().getTypeProduct()==EnumItems.CARD).toList())
				items.put((MagicCardStock)m, items.getOrDefault(m, 0)+m.getQte());

		int max = Collections.max(items.values());
		return items.entrySet().stream().filter(entry -> entry.getValue() == max).map(Entry::getKey).findAny().orElse(null);
	}
	
	public static List<MTGStockItem> validateTransaction(Transaction t) throws  IOException, SQLException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		List<MTGStockItem> rejectsT = new ArrayList<>();
		List<MTGStockItem> accepteds = new ArrayList<>();
		for(MTGStockItem transactionItem : t.getItems())
		{
				MTGStockItem stock = mtgshop.getStockById(transactionItem.getProduct().getTypeProduct(),transactionItem.getId());
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
			for(MTGStockItem stock : accepteds) 
			{
				mtgshop.saveOrUpdateStock(stock,true);
				getEnabledPlugin(MTGDao.class).saveOrUpdateOrderEntry(toOrder(t, stock));
			}
			sendMail(t,"TransactionValid"," Your order is validate !");	
			((JSONHttpServer)MTG.getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache();
		}
		else
		{
			t.setStatut(TransactionStatus.IN_PROGRESS);
		}
		
		saveTransaction(t,false);
		
		return rejectsT;
	}
	
	public static void cancelTransaction(Transaction t) throws SQLException, IOException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		
		for(MTGStockItem transactionItem : t.getItems())
		{
			MTGStockItem stock = getEnabledPlugin(MTGDao.class).getStockById(transactionItem.getProduct().getTypeProduct(),transactionItem.getId());
			if(stock==null)
				throw new SQLException("No item found for " + transactionItem.getProduct().getTypeProduct() + "="+transactionItem.getId());
				
					   stock.setQte(stock.getQte()+transactionItem.getQte());
					   stock.setUpdated(true);
					   t.setStatut(TransactionStatus.CANCELED);
					   getEnabledPlugin(MTGDao.class).saveOrUpdateStock(stock);
		}
		saveTransaction(t,false);
		((JSONHttpServer)MTG.getPlugin(new JSONHttpServer().getName(), MTGServer.class)).clearCache();
	}
	
	public static void payingTransaction(Transaction t, String providerName) throws IOException {
		t.setConfig(MTGControler.getInstance().getWebConfig());
		
		if(TransactionPayementProvider.BANK_TRANSFERT.equals(t.getPaymentProvider()) || TransactionPayementProvider.PAYPALME.equals(t.getPaymentProvider()))
			t.setStatut(TransactionStatus.PAYMENT_SENT);
		else
			t.setStatut(TransactionStatus.PAID);
		
		
		t.setPaymentProvider(TransactionPayementProvider.valueOf(providerName.toUpperCase()));
		t.setDatePayment(new Date());
		saveTransaction(t,false);
		sendMail(t,"TransactionPaid","Payment Accepted !");	
	
	}
	

	public static void sendTransaction(Transaction t) throws  IOException {
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
	
	public static boolean isAvailableFor(AbstractExternalShop shop, Transaction t) {
		for(MTGStockItem mcs : t.getItems())
		{	
			if(mcs.getTiersAppIds(shop.getName())==null)
				return false;
		}
		return true;
	}

	public static void deleteContact(Contact contact) throws IOException {
		mtgshop.deleteContact(contact);
		
	}

	public static Integer saveOrUpdateContact(Contact c) throws IOException {
		return mtgshop.saveOrUpdateContact(c);
	}

	public static List<Contact> listContacts() throws IOException {
		return mtgshop.listContacts();
	}

	public static List<Transaction> listTransactions()  throws IOException {
		return mtgshop.listTransaction();
	}

	public static void deleteTransaction(List<Transaction> t) throws IOException {
		mtgshop.deleteTransaction(t);
		
	}
	
	

}

package org.magic.services;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.notifiers.impl.EmailNotifier;
import org.magic.api.scripts.impl.JavaScript;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.MTG;

public class TransactionService
{

	public static final Integer TOKENSIZE = 50;
	protected static Logger logger = MTGLogger.getLogger(TransactionService.class);
	private static MTGExternalShop mtgshop=MTG.getEnabledPlugin(MTGExternalShop.class);
	private TransactionService() {	}

	public static int createContact(Contact c) throws IOException
	{

		c.setTemporaryToken(CryptoUtils.randomString(TOKENSIZE));
		c.setActive(false);

		int ret= mtgshop.saveOrUpdateContact(c);

		c.setTemporaryToken(MTGControler.getInstance().getWebshopService().getWebConfig().getWebsiteUrl()+"/pages/validate.html?token="+c.getTemporaryToken());

		var plug = (EmailNotifier)MTG.getPlugin(MTGConstants.EMAIL_NOTIFIER_NAME, MTGNotifier.class);
			try {
					var not = new MTGNotification("["+MTGControler.getInstance().getWebshopService().getWebConfig().getSiteTitle()+ "] Email verification", new ReportsService().generate(plug.getFormat(), c, "ContactValidation"), MTGNotification.MESSAGE_TYPE.INFO);
					plug.send(c.getEmail(),not);
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		return ret;

	}

	public static Long saveTransaction(Transaction t, boolean reloadShipping) throws IOException {
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());
		if(reloadShipping) {
			try {
				var js = new JavaScript();
				js.addVariable("total", t.total());
				js.addVariable("qty",t.getItems().size());
				js.addVariable("transaction",t);

				Object ret = js.runContent(MTGControler.getInstance().getWebshopService().getWebConfig().getShippingRules());
				t.setShippingPrice(Double.parseDouble(ret.toString()));
			} catch (Exception e1) {
				logger.error("Error updating shipping price",e1);
			}
		}

		var pContact = mtgshop.getContactByEmail(t.getContact().getEmail());

		if(pContact!=null)
		{
			t.setContact(pContact);
		}

		if(t.getContact().getId()<=0)
		{
			logger.debug("{} doesn't exist. Creating it",t.getContact());
			int id= createContact(t.getContact());
			t.getContact().setId(id);
		}
		t.getItems().forEach(it->it.setUpdated(false));
		return mtgshop.saveOrUpdateTransaction(t);
	}

	public static void sendMail(Transaction t,String template,String msg)
	{
		EmailNotifier plug = (EmailNotifier)MTG.getPlugin(MTGConstants.EMAIL_NOTIFIER_NAME, MTGNotifier.class);
		if(t.getContact().isEmailAccept())
		{
			try {
					var not = new MTGNotification("["+t.getConfig().getSiteTitle()+ "] Order #"+t.getId() + ":" + msg , new ReportsService().generate(plug.getFormat(), t, template), MTGNotification.MESSAGE_TYPE.INFO);
					plug.send(t.getContact().getEmail(),not);
				}

				catch(Exception e)
				{
					logger.error(e);
				}
		}

	}

	public static Long newTransaction(Transaction t) throws IOException {
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());
		t.setStatut(EnumTransactionStatus.NEW);
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

	public static MTGCardStock getBestProduct() throws SQLException {

		Map<MTGCardStock, Integer> items = new HashMap<>() ;
		for(var t : getEnabledPlugin(MTGDao.class).listTransactions())
			for(var m : t.getItems().stream().filter(msi->msi.getProduct().getTypeProduct()==EnumItems.CARD).toList())
				items.put((MTGCardStock)m, items.getOrDefault(m, 0)+m.getQte());

		int max = Collections.max(items.values());
		return items.entrySet().stream().filter(entry -> entry.getValue() == max).map(Entry::getKey).findAny().orElse(null);
	}

	public static List<MTGStockItem> validateTransaction(Transaction t) throws  IOException {
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());
		List<MTGStockItem> rejectsT = new ArrayList<>();
		List<MTGStockItem> accepteds = new ArrayList<>();
		for(var transactionItem : t.getItems())
		{
				var stock = mtgshop.getStockById(transactionItem.getProduct().getTypeProduct(),transactionItem.getId());
				if(transactionItem.getQte()>stock.getQte())
				{
					   t.setStatut(EnumTransactionStatus.IN_PROGRESS);
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
			t.setStatut(EnumTransactionStatus.PAYMENT_WAITING);
			for(MTGStockItem stock : accepteds)
			{
				mtgshop.saveOrUpdateStock(stock,true);
			}
			sendMail(t,"TransactionValid"," Your order is validate !");
			((JSONHttpServer)MTG.getPlugin(JSONHttpServer.JSON_HTTP_SERVER, MTGServer.class)).clearCache();
		}
		else
		{
			t.setStatut(EnumTransactionStatus.IN_PROGRESS);
		}

		saveTransaction(t,false);

		return rejectsT;
	}

	public static void cancelTransaction(Transaction t) throws SQLException, IOException {
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());

		for(MTGStockItem transactionItem : t.getItems())
		{
			MTGStockItem stock = getEnabledPlugin(MTGDao.class).getStockById(transactionItem.getProduct().getTypeProduct(),transactionItem.getId());
			if(stock==null)
				throw new SQLException("No item found for " + transactionItem.getProduct().getTypeProduct() + "="+transactionItem.getId());

					   stock.setQte(stock.getQte()+transactionItem.getQte());
					   stock.setUpdated(true);
					   t.setStatut(EnumTransactionStatus.CANCELED);
					   getEnabledPlugin(MTGDao.class).saveOrUpdateStock(stock);
		}
		saveTransaction(t,false);
		((JSONHttpServer)MTG.getPlugin(JSONHttpServer.JSON_HTTP_SERVER, MTGServer.class)).clearCache();
	}

	public static void payingTransaction(Transaction t, String providerName) throws IOException {
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());

		if(EnumPaymentProvider.BANK_TRANSFERT.equals(t.getPaymentProvider()) || EnumPaymentProvider.PAYPALME.equals(t.getPaymentProvider()))
			t.setStatut(EnumTransactionStatus.PAYMENT_SENT);
		else
			t.setStatut(EnumTransactionStatus.PAID);


		t.setPaymentProvider(EnumPaymentProvider.valueOf(providerName.toUpperCase()));
		t.setDatePayment(new Date());
		saveTransaction(t,false);
		try {
			storeInvoice(t);
		} catch (SQLException e) {
		logger.warn("Error saving invoice for {}", t,e);
		}
		sendMail(t,"TransactionPaid","Payment Accepted !");

	}


	public static void sendTransaction(Transaction t) throws  IOException {
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());
		t.setStatut(EnumTransactionStatus.SENT);
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

	public static List<Transaction> listTransactions(Contact c)  throws IOException {
		return mtgshop.listTransactions(c);
	}
	
	public static void deleteTransaction(List<Transaction> t) throws IOException {
		mtgshop.deleteTransaction(t);

	}

	public static void storeInvoice(Transaction t) throws SQLException {
		
		t.setConfig(MTGControler.getInstance().getWebshopService().getWebConfig());
		t.setCurrency(t.getConfig().getCurrency());
		
		var fileName= "invoice-"+t.getId()+".html";
		
		 if(!MTG.getEnabledPlugin(MTGDao.class).listEntries(t.getClasseName(), fileName).isEmpty())
		 {
			 logger.warn("Invoice already present for {}",t);
			 return;
		 }
		
		
		var entry = new GedEntry<Transaction>();
		  entry.setContent(new ReportsService().generate(FORMAT_NOTIFICATION.HTML, t, "Invoice").getBytes());
		  entry.setId(t.getId().toString());
		  entry.setName(fileName);
		  entry.setClasse(Transaction.class);
		  MTG.getEnabledPlugin(MTGDao.class).storeEntry(entry);
		
	}



}

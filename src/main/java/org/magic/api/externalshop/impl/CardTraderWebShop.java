package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardtrader.modele.Categorie;
import org.api.cardtrader.modele.MarketProduct;
import org.api.cardtrader.modele.Order;
import org.api.cardtrader.modele.OrderItem;
import org.api.cardtrader.modele.User;
import org.api.cardtrader.services.CardTraderConstants;
import org.api.cardtrader.services.CardTraderService;
import org.api.cardtrader.tools.URLCallInfo;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.abstracts.AbstractProduct;
import org.magic.api.beans.abstracts.AbstractStockItem;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.ProductFactory;
import org.magic.services.tools.MTG;

public class CardTraderWebShop extends AbstractExternalShop {


	private static final String TOKEN = "TOKEN";
	private CardTraderService service;


	@Override
	public String getVersion() {
		return CardTraderConstants.CARDTRADER_JAVA_API_VERSION;
	}

	private void init()
	{
		try {
		if(service==null)
			service = new CardTraderService(getAuthenticator().get(TOKEN));


		service.setForceExpansionLoadingIfNotFound(false);

		service.setListener((URLCallInfo callInfo)-> {

			var ni = new NetworkInfo();
			ni.setStart(callInfo.getStart());
			ni.setEnd(callInfo.getEnd());
			ni.setReponse(callInfo.getResponse());
			ni.setRequest(callInfo.getRequest());

			AbstractTechnicalServiceManager.inst().store(ni);


		});

		}
		catch(Exception e)
		{
			logger.error("No authenticator",e);
		}
	}


	private MTGStockItem  toItem(MarketProduct mp) {
		var prod = ProductFactory.createDefaultProduct(mp.getCategorie().getId()==1?EnumItems.CARD:EnumItems.SEALED);
		prod.setProductId(mp.getIdBlueprint().longValue());
		prod.setName(mp.getName());
		prod.setEdition(toExpansion(mp.getExpansion()));
		prod.setCategory(toCategory(mp.getCategorie()));
	
	
	var it = ProductFactory.generateStockItem(prod);
						    it.setId(mp.getId());
						    it.setAltered(mp.isAltered());
						    it.setComment("");
						    it.setFoil(mp.isFoil());
						    it.setSigned(mp.isSigned());
						    it.setLanguage(mp.getLanguage());
						    it.setQte(mp.getQty());
						    it.setPrice(mp.getPrice().getValue());
						    
						    if(mp.getCondition()!=null)
						    	it.setCondition(aliases.getReversedConditionFor(this, mp.getCondition().name(),EnumCondition.NEAR_MINT));
					
		return it;
	}

	
	
	@Override
	public List<MTGStockItem> loadStock(String search) throws IOException {
		init();

		return service.listStock(search).stream().map(this::toItem).toList();
	}


	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {

		init();
		return service.listBluePrints(name,null).stream().map(bp->{

			var product = ProductFactory.createDefaultProduct(bp.isCard()?EnumItems.CARD:EnumItems.SEALED);
				product.setName(bp.getName());
				product.setUrl(bp.getImageUrl());
				product.setProductId(bp.getId().longValue());
				product.setCategory(toCategory(bp.getCategorie()));
				product.setEdition(toExpansion(bp.getExpansion()));
				notify(product);
			return product;
		}).toList();
	}

	private MTGEdition toExpansion(org.api.cardtrader.modele.Expansion expansion) {
		if(expansion==null)
			return null;
		
		var exp = new MTGEdition();
		try {
			exp = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(expansion.getCode());
		} catch (Exception e) {
			logger.error(e);
			exp.setId(expansion.getCode());
			exp.setSet(expansion.getName());
		} 
		
		return exp;
	}

	private Category toCategory(Categorie categorie) {
		if(categorie==null)
			return null;

		var cat = new Category();
		cat.setCategoryName(categorie.getName());
		cat.setIdCategory(categorie.getId());

		return cat;
	}

	@Override
	public List<Category> listCategories() throws IOException {
		init();
		try {
		return service.listCategories().stream().map(this::toCategory).toList();
		}
		catch(Exception _)
		{
			return new ArrayList<>();
		}
	}

	@Override
	public String getName() {
		return CardTraderConstants.CARDTRADER_NAME;
	}


	private Transaction toTransaction(Order o) {
		var trans = new Transaction();
		trans.setSourceShopName(getName());
		trans.setSourceShopId(String.valueOf(o.getId()));
		trans.setId(o.getId());
		trans.setDateSend(o.getDateSend());
		trans.setDatePayment(o.getDatePaid());
		trans.setDateCreation(o.getDateCreation());

		if(o.getDatePaid()!=null)
			trans.setStatut(EnumTransactionStatus.PAID);

		if(o.getDateSend()!=null)
			trans.setStatut(EnumTransactionStatus.SENT);

		if(o.getDateCancel()!=null)
			trans.setStatut(EnumTransactionStatus.CANCELED);

		trans.setPaymentProvider(EnumPaymentProvider.SHOP_PLATEFORM);
		trans.setShippingPrice(o.getShippingMethod().getSellerPrice().getValue());
		trans.setTransporter(o.getShippingMethod().getName());
		trans.setTransporterShippingCode(o.getShippingMethod().getTrackedCode());
		
		
		o.getOrderItems().forEach(oi->trans.getItems().add(toItem(oi)));

		User u = o.getBuyer();

		if(u==null)
			u=o.getSeller();


		Contact c = new Contact();
				c.setName(u.getUsername());
				c.setAddress(o.getBillingAddress().getStreet());
				c.setZipCode(o.getBillingAddress().getZip());
				c.setCity(o.getBillingAddress().getCity());
				c.setCountry(o.getBillingAddress().getCountry());
				c.setEmail(u.getEmail());
				c.setTelephone(u.getPhone());

		trans.setContact(c);
		return trans;
		
	}

	
	private AbstractStockItem<? extends AbstractProduct> toItem(OrderItem oi) {
		AbstractStockItem<? extends AbstractProduct> item;
		if(oi.getScryfallId()!=null && !oi.getScryfallId().isEmpty()) 
		{
				try {
					var prod = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(oi.getScryfallId());
					prod.setEdition(prod.getEdition());
					item  = ProductFactory.generateStockItem(prod);
				} 
				catch (Exception e)
				{
					logger.error(e);
					var prod = ProductFactory.createDefaultProduct(EnumItems.CARD);
					prod.setName(oi.getName());
					item  = ProductFactory.generateStockItem(prod);
				}
		}
		else
		{
			var prod = ProductFactory.createDefaultProduct(EnumItems.SEALED);
			prod.setProductId(Long.valueOf(oi.getBluePrintId()));
			prod.setName(oi.getName());
			prod.setEdition(toExpansion(oi.getExpansionProduct()));
			item  = ProductFactory.generateStockItem(prod);
		}
		item.getTiersAppIds().put(getName(), ""+oi.getId());
		item.setQte(oi.getQuantity());
		item.setId(oi.getId());
		item.setPrice(oi.getPrice().getValue());
		item.setFoil(oi.isFoil());
		item.setAltered(oi.isAltered());
		item.setSigned(oi.isSigned());
		item.setCondition(aliases.getReversedConditionFor(this, oi.getCondition().name(),EnumCondition.NEAR_MINT));
		item.setLanguage(oi.getLang());
		
		return item;
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		init();
		return service.listOrders(1).stream().map(this::toTransaction).toList();
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}

	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		throw new IOException("Not possible to update " + c);
	}

	@Override
	public Contact getContactByEmail(String email) throws IOException {
		throw new IOException("Not possible to get contact from email " + email);
	}

	@Override
	public Long saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Long id) throws IOException {
		var opt = service.listStock().stream().filter(item-> item.getId()==id.intValue()).findFirst();
		
		if(opt.isPresent())
			return toItem(opt.get());
		else
			return null;
		
	}

	@Override
	public void saveOrUpdateStock(List<MTGStockItem> stock) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Contact> listContacts() throws IOException {
		return listTransaction().stream().map(Transaction::getContact).toList();
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		throw new IOException("Can't delete contact "+ contact);

	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		throw new IOException("Can't delete transaction");

	}

	@Override
	public Transaction getTransactionById(Long id) throws IOException {
		return toTransaction(service.getOrderDetails(id.intValue()));
	}

	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		return null;
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		return listTransaction().stream().filter(t->t.getContact().getId()==c.getId()).toList();
	}

	@Override
	public boolean enableContact(String token) throws IOException {
		throw new IOException("Can't enable contact ");
	}


}

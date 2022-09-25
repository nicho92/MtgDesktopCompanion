package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardtrader.enums.ConditionEnum;
import org.api.cardtrader.modele.Categorie;
import org.api.cardtrader.modele.User;
import org.api.cardtrader.services.CardTraderConstants;
import org.api.cardtrader.services.CardTraderService;
import org.api.cardtrader.tools.URLCallInfo;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.extra.AbstractProduct;
import org.magic.api.interfaces.abstracts.extra.AbstractStockItem;
import org.magic.services.TechnicalServiceManager;
import org.magic.tools.MTG;

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

			TechnicalServiceManager.inst().store(ni);


		});

		}
		catch(Exception e)
		{
			logger.error("No authenticator",e);
		}
	}

	@Override
	public List<MTGStockItem> loadStock(String search) throws IOException {
		init();

		return service.listStock(search).stream().map(mp->{
			var it = AbstractStockItem.generateDefault();
								    it.setId(mp.getId());
								    it.setAltered(mp.isAltered());
								    it.setComment("");
								    it.setFoil(mp.isFoil());
								    it.setSigned(mp.isSigned());
								    it.setLanguage(mp.getLanguage());
								    it.setQte(mp.getQty());
								    it.setPrice(mp.getPrice().getValue());
								var prod = AbstractProduct.createDefaultProduct();
								prod.setProductId(mp.getIdBlueprint().longValue());
								prod.setName(mp.getName());
								prod.setEdition(toExpansion(mp.getExpansion()));
								prod.setCategory(toCategory(mp.getCategorie()));
								prod.setTypeProduct(prod.getName().contains("Booster")?EnumItems.SEALED:EnumItems.CARD);
								it.setProduct(prod);
								return (MTGStockItem)it;
		}).toList();
	}


	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {

		init();
		return service.listBluePrintsByIds(null, name, null).stream().map(bp->{

			var product = AbstractProduct.createDefaultProduct();
				product.setName(bp.getName());
				product.setUrl(bp.getImageUrl());
				product.setProductId(bp.getId().longValue());
				product.setCategory(toCategory(bp.getCategorie()));
				product.setEdition(toExpansion(bp.getExpansion()));
				notify(product);
			return product;
		}).toList();
	}

	private MagicEdition toExpansion(org.api.cardtrader.modele.Expansion expansion) {
		if(expansion==null)
			return null;
		var exp = new MagicEdition();
		exp.setId(expansion.getCode());
		exp.setSet(expansion.getName());
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
		catch(Exception e)
		{
			return new ArrayList<>();
		}
	}

	@Override
	public String getName() {
		return CardTraderConstants.CARDTRADER_NAME;
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		init();
		return service.listOrders(1).stream().map(o->{
			var trans = new Transaction();
			trans.setId(o.getId());
			trans.setDateSend(o.getDateSend());
			trans.setDatePayment(o.getDatePaid());
			trans.setDateCreation(o.getDateCreation());

			if(o.getDatePaid()!=null)
				trans.setStatut(TransactionStatus.PAID);


			if(o.getDateSend()!=null)
				trans.setStatut(TransactionStatus.SENT);

			if(o.getDateCancel()!=null)
				trans.setStatut(TransactionStatus.CANCELED);



			o.getOrderItems().forEach(oi->{

				var item  = AbstractStockItem.generateDefault();

				item.setPrice(oi.getPrice().getValue());
				item.setId(oi.getId());
				item.setQte(oi.getQuantity());

				if(oi.getScryfallId()!=null && !oi.getScryfallId().isEmpty()) {
						try {
							var prod = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(oi.getScryfallId());
							prod.setEdition(prod.getCurrentSet());
							item.setProduct(prod);
						} catch (Exception e)
						{
							logger.error(e);
							var prod = AbstractProduct.createDefaultProduct();
							prod.setName(oi.getName());
							item.setProduct(prod);
						}
				}
				else
				{
					var prod = AbstractProduct.createDefaultProduct();
					prod.setName(oi.getName());
					item.setProduct(prod);
				}
				item.setFoil(oi.isFoil());
				item.setAltered(oi.isAltered());
				item.setSigned(oi.isSigned());
				item.setCondition(parseCondition(oi.getCondition()));
				item.setLanguage(oi.getLang());


				trans.getItems().add(item);

			});

			trans.setSourceShopName(getName());

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
		}).toList();
	}


	private EnumCondition parseCondition(ConditionEnum condition) {

		switch(condition)
		{
		case HEAVILY_PLAYED: return EnumCondition.DAMAGED;
		case MINT:return EnumCondition.MINT;
		case MODERATELY_PLAYED:return EnumCondition.PLAYED;
		case NEAR_MINT:return EnumCondition.NEAR_MINT;
		case PLAYED:return EnumCondition.PLAYED;
		case POOR:return EnumCondition.POOR;
		case SLIGHTLY_PLAYED:return EnumCondition.LIGHTLY_PLAYED;
		}

		return null;

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
		// TODO Auto-generated method stub
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
	public Transaction getTransactionById(Long parseInt) throws IOException {
		// TODO Auto-generated method stub
		return null;
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

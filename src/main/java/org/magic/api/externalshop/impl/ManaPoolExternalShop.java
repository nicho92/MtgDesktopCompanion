package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.api.manapool.listener.URLCallInfo;
import org.api.manapool.model.InventoryItem;
import org.api.manapool.model.Order;
import org.api.manapool.model.ProductItem;
import org.api.manapool.model.enums.EnumFinish;
import org.api.manapool.model.enums.EnumType;
import org.api.manapool.services.ManaPoolAPIService;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.ProductFactory;
import org.magic.services.tools.MTG;

public class ManaPoolExternalShop extends AbstractExternalShop {

	private ManaPoolAPIService service;

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		
		
		return new ArrayList<>();
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, String id) throws IOException {
		init();
		
		var type = (typeStock==EnumItems.CARD?EnumType.SINGLE:EnumType.SEALED);
		var item = service.getSellerInventoryById(type, id);
			
		return convert(item);
	}

	@Override
	public List<Category> listCategories() throws IOException {
		return Stream.of(EnumType.values()).map(t->new Category(t==EnumType.SINGLE?1:2, t.name())).toList();
	}

	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getContactByEmail(String email) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contact> listContacts() throws IOException {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Contact getContactByLogin(String login, String passw) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		init();
		
	}

	@Override
	public Transaction getTransactionById(String id) throws IOException {
		
		init();
		
		return convert(service.getBoughtOrderById(id));
		
		
	}


	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public boolean enableContact(String token) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "ManaPool";
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		init();
	
		return new ArrayList<>();
	}

	@Override
	protected List<MTGStockItem> loadStock(String search) throws IOException {
		init();
		
		var ret = new ArrayList<MTGStockItem>();
		
		service.getSellerInventory().forEach(item->{
			try {
				
				if(item.getProduct().getName().contains(search) || StringUtils.isEmpty(search))
					ret.add(convert(item));
				
			} catch (IOException e) {
				logger.error(e);
			}
		});
		return ret;
	}

	private void init() {
		if(service==null)
			service = new ManaPoolAPIService(getAuthenticator().get("EMAIL"), getAuthenticator().get("TOKEN"));
		
		service.getClient().setCallListener((URLCallInfo callInfo)->{
			var netinfo = new NetworkInfo();
				netinfo.setStart(callInfo.getStart());
				netinfo.setEnd(callInfo.getEnd());
				netinfo.setRequest(callInfo.getRequest());
				netinfo.setReponse(callInfo.getResponse());

			AbstractTechnicalServiceManager.inst().store(netinfo);

	});
		
		
	}
	

	private Transaction convert(Order o) {
		var t  = new Transaction();
		
		t.setCurrency(Currency.getInstance("USD"));
		t.setDateCreation(o.getCreatedAtDate());
		t.setTransporter(o.getShippingMethod());
		t.setTypeTransaction(EnumTransactionDirection.SELL);
		
		
		o.getItems().forEach(item->{
			
			try {
				var it = convert(item);
				t.getItems().add(it);
			} catch (IOException e) {
				logger.error(e);
			}
			
		});
		
		
		
		return t;
		
		
	}
	
	private MTGStockItem convert(ProductItem obj) throws IOException
	{

		if(obj.getType()==EnumType.SINGLE)
		{
		var card = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(obj.getSingle().getScryfallId());
		var item = ProductFactory.generateStockItem(card);
			 item.setFoil(obj.getSingle().getFinishId()==EnumFinish.FO);
			 item.setEdition(card.getEdition());
			 item.setEtched(obj.getSingle().getFinishId()==EnumFinish.EF);
			 item.setLanguage(obj.getLanguage().getLabel());
			 item.getTiersAppIds().put(getName(), obj.getId());
			 
			 return item;
		}
		else
		{
			var sealed = new MTGSealedProduct();
			sealed.setName(obj.getName());
			sealed.setEdition(MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(obj.getSealed().getSet()));
			
			if(obj.getName().contains("Booster Box"))
				sealed = MTG.getEnabledPlugin(MTGSealedProvider.class).get(sealed.getEdition(), EnumItems.BOX).get(0);
			else if(obj.getName().contains("Booster Pack"))
				sealed = MTG.getEnabledPlugin(MTGSealedProvider.class).get(sealed.getEdition(), EnumItems.BOOSTER).get(0);

			
			
			
			var item = ProductFactory.generateStockItem(sealed);
			 item.setLanguage(obj.getLanguage().getLabel());
			 item.getTiersAppIds().put(getName(), obj.getId());
			 item.setEdition(sealed.getEdition());
			 return item;
		}
	}
	

	private MTGStockItem convert(InventoryItem obj) throws IOException {
		
					var item = convert(obj.getProduct());
					item.setPrice(obj.getPriceValue());
					return item;
	}

	@Override
	protected void saveOrUpdateStock(List<MTGStockItem> items) throws IOException {
		
		init();
		
		
	}

}

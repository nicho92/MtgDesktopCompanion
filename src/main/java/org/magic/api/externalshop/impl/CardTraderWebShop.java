package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardtrader.modele.Categorie;
import org.api.cardtrader.services.CardTraderService;
import org.api.mkm.modele.Category;
import org.api.mkm.modele.Expansion;
import org.api.mkm.modele.Product;
import org.magic.api.beans.Contact;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;

public class CardTraderWebShop extends AbstractExternalShop {

	
	private static final String TOKEN = "TOKEN";
	private CardTraderService service;
	
	
	private void init()
	{
		try {
		if(service==null)
			service = new CardTraderService(getAuthenticator().get(TOKEN));
		}
		catch(Exception e)
		{
			logger.error("No authenticator "+e);
		}
	}
	
	@Override
	public List<MTGStockItem> loadStock(String search) throws IOException {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}
	
	
	@Override
	public List<Product> listProducts(String name) throws IOException {
		
		init();
		return service.listBluePrintsByIds(null, name, null).stream().map(bp->{
			
			var product = new Product();
				product.setEnName(bp.getName());
				product.setImage(bp.getImageUrl());
				product.setIdProduct(bp.getId());
				product.setCategory(toCategory(bp.getCategorie()));				
				product.setCategoryName(product.getCategory().getCategoryName());
				product.setLocalization(new ArrayList<>());
				product.setExpansion(toExpansion(bp.getExpansion()));
				product.setExpansionName(product.getExpansion().getEnName());
				
				
				notify(product);
				
			return product;
			
		}).toList();
	}

	private Expansion toExpansion(org.api.cardtrader.modele.Expansion expansion) {
		var exp = new Expansion();
		
		exp.setAbbreviation(expansion.getCode());
		exp.setEnName(expansion.getName());
		exp.setIdExpansion(expansion.getId());
		
		return exp;
	}

	private Category toCategory(Categorie categorie) {
		var cat = new Category();
		cat.setCategoryName(categorie.getName());
		cat.setIdCategory(categorie.getId());
		
		return cat;
	}

	@Override
	protected void createTransaction(Transaction t) throws IOException {
		throw new IOException("Can't create transation to " + getName());

	}

	@Override
	public int createProduct(Product t, Category c) throws IOException {
		throw new IOException("Can't create product to " + getName());
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
		return "CardTrader";
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		init();
		return service.listOrders(1).stream().map(o->{
			var trans = new Transaction();
			trans.setId(o.getId());
			trans.setDateSend(o.getDateSend());
			trans.setDatePayment(o.getDateCreditAddedToSeller());
			trans.setSourceShopName(getName());
			
			Contact c = new Contact();
					c.setName(o.getBuyer().getUsername());
					
					
					c.setAddress(o.getBillingAddress().getStreet());
					c.setZipCode(o.getBillingAddress().getZip());
					c.setCity(o.getBillingAddress().getCity());
					c.setCountry(o.getBillingAddress().getCountry());
					c.setEmail(o.getBuyer().getEmail());
					c.setTelephone(o.getBuyer().getPhone());	
					
			trans.setContact(c);
		
			return trans;
		}).toList();
	}
	

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
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
	public int saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Integer id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateStock(EnumItems typeStock, MTGStockItem stock) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Contact> listContacts() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteContact(Contact contact) throws IOException {
		// TODO Auto-generated method stub
		
	}
	

}

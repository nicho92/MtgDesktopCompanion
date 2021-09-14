package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.api.cardtrader.modele.Categorie;
import org.api.cardtrader.services.CardTraderService;
import org.api.mkm.modele.Category;
import org.api.mkm.modele.Expansion;
import org.api.mkm.modele.Product;
import org.magic.api.beans.Contact;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;

public class CardTraderWebShop extends AbstractExternalShop {

	
	private static final String TOKEN = "TOKEN";
	private CardTraderService service;
	
	public CardTraderWebShop() {
		service = new CardTraderService(getString(TOKEN));
	}
	
	@Override
	public List<Product> listProducts(String name) throws IOException {
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
			
		}).collect(Collectors.toList());
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
		return service.listCategories().stream().map(this::toCategory).collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return "CardTrader";
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		return service.listOrders().stream().map(o->{
			var trans = new Transaction();
			trans.setId(o.getId());
			trans.setDateSend(o.getDateSend());
			trans.setDatePayment(o.getDateCreditAddedToSeller());
			trans.setSourceShopNmae(getName());
			Contact c = new Contact();
					c.setName(o.getBuyer().getUsername());
					
			trans.setContact(c);
		
			return trans;
		}).collect(Collectors.toList());
	}
	

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}
	

}

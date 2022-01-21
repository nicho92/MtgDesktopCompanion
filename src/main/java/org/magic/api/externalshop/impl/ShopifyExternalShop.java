package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;

import com.shopify.ShopifySdk;

public class ShopifyExternalShop extends AbstractExternalShop {

	public static void main(String[] args) {
		
		var pass="";
		
		var build = ShopifySdk.newBuilder()
				  .withSubdomain("")
				  .withAccessToken(pass).build();
	 
		
		var products= build.getProducts();
		
		for(var prod :products.values())
		{
			System.out.println(prod.getTitle());
				
		}
		
	}
	
	
	
	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createProduct(MTGProduct t, Category c) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("SUBDOMAIN", "ACCESS_TOKEN") ;
	}
	
	
	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Integer id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> listCategories() throws IOException {
		// TODO Auto-generated method stub
		return null;
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
		return null;
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
	public int saveOrUpdateTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteTransaction(Transaction t) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction getTransactionById(int parseInt) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions(Contact c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enableContact(String token) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "Shopify";
	}

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<MTGStockItem> loadStock(String search) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void saveOrUpdateStock(List<MTGStockItem> it) throws IOException {
		// TODO Auto-generated method stub

	}

}

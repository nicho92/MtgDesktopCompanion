package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;

public interface MTGExternalShop extends MTGPlugin {
	
	
	
	
	public List<MTGProduct> listProducts(String name) throws IOException;
	public int createProduct(MTGProduct t,Category c) throws IOException;
	public int createProduct(MTGExternalShop input, MTGProduct t, String lang,Category c)throws IOException;
	
	
	public List<MTGStockItem> listStock(String search) throws IOException;
	public MTGStockItem getStockById(EnumItems typeStock,Integer id)throws IOException;
	public void saveOrUpdateStock(MTGStockItem stock)throws IOException;
	public void saveOrUpdateStock(List<MTGStockItem> stocks)throws IOException;
	
	
	public List<Category> listCategories() throws IOException;
	
	public Integer saveOrUpdateContact(Contact c) throws IOException;
	public Contact getContactByEmail(String email) throws IOException;
	public List<Contact> listContacts() throws IOException;
	public void deleteContact(Contact contact) throws IOException;
	public Contact getContactByLogin(String login, String passw) throws IOException;

	
	public int saveOrUpdateTransaction(Transaction t) throws IOException;
	public void createTransaction(Transaction t, boolean automaticProductCreation) throws IOException;
	public List<Transaction> listTransaction() throws IOException;
	public void deleteTransaction(Transaction t) throws IOException;
	public void deleteTransaction(List<Transaction> t) throws IOException;
	public Transaction getTransactionById(int parseInt) throws IOException;
	
	public void updateConversion(String name, String destName, String language, Integer idProduct, int idDestProduct) throws IOException;
	public List<Transaction> listTransactions(Contact c) throws IOException;
	public boolean enableContact(String token)throws IOException;
	
}

package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;

public interface MTGExternalShop extends MTGPlugin {




	public List<MTGProduct> listProducts(String name) throws IOException;


	public List<MTGStockItem> listStock(String search) throws IOException;
	public MTGStockItem getStockById(EnumItems typeStock,Long id)throws IOException;
	public void saveOrUpdateStock(MTGStockItem stock,boolean allShop)throws IOException;
	public void saveOrUpdateStock(List<MTGStockItem> stocks,boolean allShop)throws IOException;
	public void updateStockFromTransaction(Transaction t) throws IOException;


	public List<Category> listCategories() throws IOException;
	public Category getCategoryById(Integer id) throws IOException;
	
	public Integer saveOrUpdateContact(Contact c) throws IOException;
	public Contact getContactByEmail(String email) throws IOException;
	public List<Contact> listContacts() throws IOException;
	public void deleteContact(Contact contact) throws IOException;
	public Contact getContactByLogin(String login, String passw) throws IOException;


	public Long saveOrUpdateTransaction(Transaction t) throws IOException;
	public List<Transaction> listTransaction() throws IOException;
	public void deleteTransaction(Transaction t) throws IOException;
	public void deleteTransaction(List<Transaction> t) throws IOException;
	public Transaction getTransactionById(Long id) throws IOException;

	public List<Transaction> listTransactions(Contact c) throws IOException;
	public boolean enableContact(String token)throws IOException;

}

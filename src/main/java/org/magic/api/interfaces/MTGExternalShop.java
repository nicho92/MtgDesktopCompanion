package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.api.mkm.modele.Category;
import org.api.mkm.modele.Product;
import org.magic.api.beans.Transaction;

public interface MTGExternalShop extends MTGPlugin {
	
	
	
	public List<Transaction> listTransaction() throws IOException;
	public List<Product> listProducts(String name) throws IOException;
	public List<MTGStockItem> listStock(String search) throws IOException;
	public int createProduct(Product t,Category c) throws IOException;
	public int createProduct(MTGExternalShop input, Product t, String lang,Category c)throws IOException;
	public List<Category> listCategories() throws IOException;
	public void createTransaction(Transaction t, boolean automaticProductCreation) throws IOException;
	
}

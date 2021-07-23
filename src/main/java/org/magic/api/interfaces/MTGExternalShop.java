package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.api.mkm.modele.Product;
import org.magic.api.beans.Transaction;

public interface MTGExternalShop extends MTGPlugin {
	
	
	
	public List<Transaction> listTransaction() throws IOException;
	public List<Product> listProducts(String name) throws IOException;
	public void createTransaction(Transaction t) throws IOException;
	public int createProduct(Product t) throws IOException;
	public int createProduct(MTGExternalShop ext, Product t, String lang)throws IOException;
}

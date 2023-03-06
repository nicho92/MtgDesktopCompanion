package org.magic.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;

public interface MTGShopper extends MTGPlugin {

	public List<RetrievableTransaction> listOrders() throws IOException;
	public Transaction getTransaction(RetrievableTransaction rt ) throws IOException; 
	public Transaction getTransactionById(String id) throws IOException;
	
	public List<Transaction> listTransactions(List<RetrievableTransaction> rt ) throws IOException; 
}

package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.api.mkm.modele.Product;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;

public class MTGCompanionShop extends AbstractExternalShop {

	@Override
	public List<Transaction> loadTransaction() throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listTransactions();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<Product> listProducts(String name) throws IOException {
		//TODO Load sealed and stockcards
		return new ArrayList<>();
	}

	@Override
	public void createTransaction(Transaction t) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public int createProduct(Product t) throws IOException {
		throw new IOException("not implemented" + t); 
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGConstants.IMAGE_LOGO);
	}
	
	@Override
	public String getName() {
		return MTGConstants.MTG_APP_NAME;
	}



}

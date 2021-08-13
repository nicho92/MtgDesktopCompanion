package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.api.mkm.modele.Category;
import org.api.mkm.modele.Product;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGConstants;
import org.magic.services.TransactionService;
import org.magic.services.providers.SealedProductProvider;
import org.magic.tools.MTG;
import org.utils.patterns.observer.Observer;

import com.thoughtworks.xstream.XStream;

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
	public List<Category> listCategories() throws IOException {
		
		List<Category> cat = new ArrayList<>();
		
		int i=1;
		for(EnumItems item : EnumItems.values())
		{
			Category c = new Category();
					 c.setIdCategory(i++);
					 c.setCategoryName(item.name());
			cat.add(c);
		}
		
		return cat;
	}


	@Override
	public List<Product> listProducts(String name) throws IOException {
		
		List<MagicCard> cards = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, null, false);
		List<MTGSealedProduct> products = SealedProductProvider.inst().search(name);
		
		
		logger.debug("Found " + products + " for " + name);
		var ret = new ArrayList<Product>();
		
		cards.forEach(card->{
			Product p = new Product();
					p.setEnName(card.getName());
					p.setExpansionName(card.getCurrentSet().getSet());
					p.setCategoryName(EnumItems.CARD.name());
					p.setImage(MTG.getEnabledPlugin(MTGPictureProvider.class).generateUrl(card));
					p.setGameName("Magic: The Gathering");
					p.setNumber(card.getCurrentSet().getNumber());
					p.setIdProduct(card.getId().hashCode());
					notify(p);
					ret.add(p);
		});
		
		products.forEach(ss->{
			Product p = new Product();
					p.setEnName(ss.getType() + " " + (ss.getExtra()!=null ? ss.getExtra():"")+ " "  + ss.getEdition() + " " + ss.getLang());
					p.setGameName("Magic: The Gathering");
					p.setImage(ss.getUrl());
					p.setIdProduct(ss.hashCode());
					p.setCategoryName(EnumItems.SEALED.name());
					p.setExpansionName(ss.getEdition().getSet());
					notify(p);
			ret.add(p);
		});
		
		return ret;
	}

	@Override
	public void createTransaction(Transaction t) throws IOException {
		try {
			t.setId(-1);
			t.getContact().setId(-1);
			TransactionService.saveTransaction(t, false);
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public int createProduct(Product t,Category c) throws IOException {
		
		logger.debug("adding " + t);
		return -1;
	//	throw new IOException("not implemented " + t); 
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

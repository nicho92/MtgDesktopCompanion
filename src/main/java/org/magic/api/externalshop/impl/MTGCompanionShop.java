package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.api.mkm.modele.Category;
import org.api.mkm.modele.Product;
import org.magic.api.beans.Contact;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.services.MTGConstants;
import org.magic.services.TransactionService;
import org.magic.services.providers.SealedProductProvider;
import org.magic.tools.MTG;

public class MTGCompanionShop extends AbstractExternalShop {

	@Override
	protected List<Transaction> loadTransaction() throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listTransactions();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public List<Category> listCategories() throws IOException {
		
		var cat = new ArrayList<Category>();
		
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
	public List<MTGStockItem> loadStock(String search) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listStockItems().stream().filter(msi->msi.getProductName().contains(search)).toList();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<Product> listProducts(String name) throws IOException {
		
		var cards = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, null, false);
		var products = SealedProductProvider.inst().search(name);
		
		
		logger.debug("Found " + products + " for " + name);
		var ret = new ArrayList<Product>();
		
		cards.forEach(card->{
			var p = new Product();
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
			var p = new Product();
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
	protected void createTransaction(Transaction t) throws IOException {
			t.setId(-1);
			t.getContact().setId(-1);
			TransactionService.saveTransaction(t, false);
	}

	@Override
	public int createProduct(Product t,Category c) throws IOException {
		throw new IOException("not implemented " + t); 
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGConstants.IMAGE_LOGO);
	}
	
	@Override
	public String getName() {
		return MTGConstants.MTG_APP_NAME;
	}
	
	@Override
	public void deleteContact(Contact contact) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteContact(contact);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
	}
	@Override
	public Integer saveOrUpdateContact(Contact c) throws IOException  {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateContact(c);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	@Override
	public List<Contact> listContacts() throws IOException  {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listContacts();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	public Contact getContactByEmail(String email) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).getContactByEmail(email);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	@Override
	public int saveOrUpdateTransaction(Transaction t)  throws IOException {
		try {
		return MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateTransaction(t);
		}
		catch(SQLException e)
		{
			throw new IOException(e);
		}
	}
	@Override
	public MTGStockItem getStockById(EnumItems typeStock, Integer id) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).getStockById(typeStock,id);
			}
			catch(SQLException e)
			{
				throw new IOException(e);
			}
	}
	@Override
	public void saveOrUpdateStock(EnumItems typeStock, MTGStockItem stock) throws IOException {
		try {
			 MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateStock(typeStock,stock);
			}
			catch(SQLException e)
			{
				throw new IOException(e);
			}
		
	}



}

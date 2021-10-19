package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGProduct;
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
			return MTG.getEnabledPlugin(MTGDao.class).listStockItems().stream().filter(msi->msi.getProduct().getName().contains(search)).toList();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<MTGProduct> listProducts(String name) throws IOException {
		
		var cards = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, null, false);
		var products = SealedProductProvider.inst().search(name);
		
		
		logger.debug("Found " + products + " for " + name);
		var ret = new ArrayList<MTGProduct>();
		
		cards.forEach(card->{
					card.setEdition(card.getCurrentSet());
					card.setCategory(new Category(0, EnumItems.CARD.name()));
					card.setUrl(MTG.getEnabledPlugin(MTGPictureProvider.class).generateUrl(card));
					card.setProductId(""+card.getId().hashCode());
					notify(card);
					ret.add(card);
		});
		
		products.forEach(ss->{
					ss.setName(ss.getTypeProduct() + " " + (ss.getExtra()!=null ? ss.getExtra():"")+ " "  + ss.getEdition() + " " + ss.getLang());
					ss.setUrl(ss.getUrl());
					ss.setProductId(""+ss.hashCode());
					ss.setCategory(new Category(1,EnumItems.SEALED.name()));
					notify(ss);
			ret.add(ss);
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
	public int createProduct(MTGProduct t,Category c) throws IOException {
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

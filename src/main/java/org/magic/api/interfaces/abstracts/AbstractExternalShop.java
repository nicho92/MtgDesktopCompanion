package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.api.mkm.modele.Category;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.Product;
import org.magic.api.beans.ConverterItem;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.tools.MTG;

public abstract class AbstractExternalShop extends AbstractMTGPlugin implements MTGExternalShop {

	
	protected abstract List<Transaction> loadTransaction() throws IOException;
	protected abstract List<MTGStockItem> loadStock(String search) throws IOException;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EXTERNAL_SHOP;
	}
	
	protected abstract void createTransaction(Transaction t) throws IOException;

	
	public List<ConverterItem> getRefs(String lang, int id)
	{
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listConversionItems().stream().filter(p->(p.getLang().equalsIgnoreCase(lang) && (p.getInputId()==id || p.getOutputId()==id))).toList();
		} catch (SQLException e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}
	
	
	@Override
	public List<Transaction> listTransaction() throws IOException {
		
		var list= loadTransaction();
		list.forEach(t->
			t.getItems().forEach(item->{
				getRefs(item.getLanguage(),item.getId()).forEach(converterItem->item.getTiersAppIds().put(converterItem.getDestination(),String.valueOf(converterItem.getOutputId())));
				getRefs(item.getLanguage(),item.getId()).forEach(converterItem->item.getTiersAppIds().put(converterItem.getSource(),String.valueOf(converterItem.getInputId())));
			})
			);
		
		return list;
	}

	@Override
	public List<MTGStockItem> listStock(String search) throws IOException {
		var list= loadStock(search);
		list.forEach(item->{
			getRefs(item.getLanguage(),item.getId()).forEach(converterItem->item.getTiersAppIds().put(converterItem.getDestination(),String.valueOf(converterItem.getOutputId())));
			getRefs(item.getLanguage(),item.getId()).forEach(converterItem->item.getTiersAppIds().put(converterItem.getSource(),String.valueOf(converterItem.getInputId())));
		});
			
		return list;
	}
	
	
	@Override
	public void createTransaction(Transaction t, boolean automaticProductCreation) throws IOException {
	
		logger.debug("Creating transaction " + t.getSourceShopName() +" in " + getName());
		
		t.getItems().forEach(mci->{
			
			if( automaticProductCreation &&  mci.getTiersAppIds(getName())==null)
			{
				Product p = new Product();
							 p.setEnName(mci.getProduct().getName());
							 p.setImage(mci.getUrl());
							 
				Category c = new Category();
								c.setIdCategory(172);
								c.setCategoryName("Test");
				try {
					int ret = createProduct(p,c);
					
					if(ret>0)
					{
						mci.getTiersAppIds().put(getName(), String.valueOf(ret));
						MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(new ConverterItem( t.getSourceShopName(),getName(), mci.getProduct(), mci.getLanguage(), mci.getId(),ret));
					}
					
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		
		createTransaction(t);
			
	
	}
	
	
	
	@Override
	public int createProduct(MTGExternalShop input, Product t,String lang,Category c) throws IOException {
		Localization defaultLoc = new Localization(1, "English");
		defaultLoc.setName(t.getEnName());
		String locName = t.getLocalization().stream().filter(l->l.getLanguageName().equalsIgnoreCase(lang)).findFirst().orElse(defaultLoc).getName();
		t.setEnName(locName);
		int ret = createProduct(t,c);
		try {
			
			updateConversion(input.getName(), locName,lang, t.getIdProduct(), ret);
		} catch (IOException e) {
			throw new IOException(e);
		}
		return ret;
	}

	@Override
	public  void updateConversion(String sourcename, String productName, String language, Integer idProduct, int idDestProduct ) throws IOException
	{
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(new ConverterItem(sourcename,getName(), productName,language, idProduct, idDestProduct));
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	
}



package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.api.mkm.modele.Category;
import org.api.mkm.modele.Localization;
import org.api.mkm.modele.Product;
import org.magic.api.beans.ConverterItem;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.tools.MTG;

public abstract class AbstractExternalShop extends AbstractMTGPlugin implements MTGExternalShop {

	
	protected abstract List<Transaction> loadTransaction() throws IOException;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EXTERNAL_SHOP;
	}
	
	protected abstract void createTransaction(Transaction t) throws IOException;
	
	
	public List<ConverterItem> getOutputRefs(String lang, String sourceName, int idSource)
	{
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listConversionItems().stream().filter(p->(p.getSource().equalsIgnoreCase(sourceName) && p.getLang().equalsIgnoreCase(lang) && p.getInputId()==idSource)).collect(Collectors.toList());
		} catch (SQLException e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}
	
	
	@Override
	public List<Transaction> listTransaction() throws IOException {
		
		var list= loadTransaction();
		list.forEach(t->
			t.getItems().forEach(item->
					getOutputRefs(item.getLanguage(),getName(),item.getId()).forEach(converterItem->item.getTiersAppIds().put(converterItem.getDestination(),String.valueOf(converterItem.getOutputId()))	
				)
			)
		);
		return list;
	}

	@Override
	public void createTransaction(Transaction t, boolean automaticProductCreation) throws IOException {
	
		logger.debug("Creating transaction " + t.getSourceShopName() +" in " + getName());
		
		t.getItems().forEach(mci->{
			
			if( automaticProductCreation &&  mci.getTiersAppIds(getName())==null)
			{
				Product p = new Product();
							 p.setEnName(mci.getProductName());
							 p.setImage(mci.getUrl());
							 
				Category c = new Category();
								c.setIdCategory(172);
								c.setCategoryName("Test");
				try {
					int ret = createProduct(p,c);
					
					if(ret>0)
					{
						mci.getTiersAppIds().put(getName(), String.valueOf(ret));
						System.out.println(mci.getPrice());
						MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(new ConverterItem( t.getSourceShopName(),getName(), mci.getProductName(), mci.getLanguage(), mci.getId(),ret));
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
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateConversionItem(new ConverterItem( input.getName(),getName(), locName,lang, t.getIdProduct(), ret));
		} catch (SQLException e) {
			throw new IOException(e);
		}
		return ret;
	}

}



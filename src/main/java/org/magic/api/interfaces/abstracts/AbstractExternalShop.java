package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.api.mkm.modele.Localization;
import org.api.mkm.modele.Product;
import org.magic.api.beans.ConverterItem;
import org.magic.api.beans.Transaction;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.services.providers.StockItemConversionManager;

public abstract class AbstractExternalShop extends AbstractMTGPlugin implements MTGExternalShop {

	
	protected StockItemConversionManager converter;
	protected abstract List<Transaction> loadTransaction() throws IOException;

	protected AbstractExternalShop() {
		converter = new StockItemConversionManager();
		try {
			converter.initFile(new File("C:\\Users\\Nicolas\\Google Drive\\conversions.csv"));
			} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EXTERNAL_SHOP;
	}
	
	
	@Override
	public List<Transaction> listTransaction() throws IOException {
		
		var list= loadTransaction();
		list.forEach(t->
			t.getItems().forEach(item->
				converter.getOutputRefs(item.getLanguage(),getName(),item.getId()).forEach(converterItem->
					item.getTiersAppIds().put(converterItem.getDestination(),String.valueOf(converterItem.getOutputId()))	
				)
			)
		);
		return list;
	}

	
	@Override
	public int createProduct(MTGExternalShop ext, Product t,String lang) throws IOException {
		Localization defaultLoc = new Localization(1, "English");
		defaultLoc.setName(t.getEnName());
		String locName = t.getLocalization().stream().filter(l->l.getLanguageName().equalsIgnoreCase(lang)).findFirst().orElse(defaultLoc).getName();
		t.setEnName(locName);
		
		int ret = createProduct(t);
		converter.appendConversion(new ConverterItem( ext.getName(),getName(), locName,lang, t.getIdProduct(), ret));
		return ret;
	}

}



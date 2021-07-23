package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	public int createProduct(MTGExternalShop ext, Product t) throws IOException {
		int ret = createProduct(t);
		converter.appendConversion(new ConverterItem( ext.getName(),getName(), t.getEnName(),t.getLocalization().get(0).getLanguageName(), t.getIdProduct(), ret));
		return ret;
	}

}



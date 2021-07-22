package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.modele.Product;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.ConverterItem;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.providers.StockItemConversionManager;
import org.magic.tools.MTG;

public class Mkm2WooCommerce {
	
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
	private MTGExternalShop mkm;
	private MTGExternalShop woo;
	private StockItemConversionManager converter;
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance();
		
		new Mkm2WooCommerce().listTransaction().forEach(t->{
				System.out.println(t.getContact() + " "+ t.total() + " " + t.getCurrency());
				t.getItems().forEach(item->{
					System.out.println("\t"+item.getProductName() + " " + item.getTiersAppIds());
				});
			
		});
		
	}


	public Mkm2WooCommerce() throws IOException {
		try {
			converter = new StockItemConversionManager();
			converter.loadConversions(new File("C:\\Users\\Pihen\\Downloads\\conversions.csv"));
		} catch (Exception e) {
			logger.error(e);
		}
		mkm = MTG.getPlugin(MkmConstants.MKM_NAME, MTGExternalShop.class);
		woo = MTG.getPlugin(WooCommerceExport.WOO_COMMERCE, MTGExternalShop.class);
	}
	
	private void testTransaction()
	{
		try {
			Transaction t = mkm.listTransaction().get(0);
			woo.createTransaction(t);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	
	public List<Transaction> listTransaction() throws IOException
	{
		List<Transaction> list = mkm.listTransaction();
		
		list.forEach(t->
			t.getItems().forEach(item->
				item.getTiersAppIds().put(woo.getName(),String.valueOf(converter.getOutputId(item.getLanguage(),Integer.parseInt(item.getTiersAppIds().get(mkm.getName())))))
			)
		);
		return list;
	}
	
	
	private int exportProduct(Product p) throws IOException {
		int ret = woo.createProduct(p);
		converter.appendConversion(new ConverterItem(MkmConstants.MKM_NAME,WooCommerceExport.WOO_COMMERCE,p.getEnName(), p.getIdProduct(), ret, "English"));
		return ret;
	}
	
	
	
}



class MkmStockItem extends AbstractStockItem<LightArticle>
{
	private static final long serialVersionUID = 1L;

	@Override
	public void setProduct(LightArticle product) {
		this.product=product;
		setProductName(product.getProduct().getEnName());
		edition= new MagicEdition("",product.getProduct().getExpansion());
		url = "https:"+ product.getProduct().getImage();
		setTypeStock(EnumItems.SEALED);
	}

}

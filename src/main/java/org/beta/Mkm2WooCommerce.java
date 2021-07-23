package org.beta;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.api.mkm.modele.LightArticle;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Transaction;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.exports.impl.WooCommerceExport;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.abstracts.AbstractStockItem;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.MTG;

public class Mkm2WooCommerce {
	
	private static Logger logger = MTGLogger.getLogger(Mkm2WooCommerce.class);
	private MTGExternalShop mkm;
	private MTGExternalShop woo;
	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance();
		
		new Mkm2WooCommerce();
	}


	public Mkm2WooCommerce() throws IOException {
		mkm = MTG.getPlugin(MkmConstants.MKM_NAME, MTGExternalShop.class);
		woo = MTG.getPlugin(WooCommerceExport.WOO_COMMERCE, MTGExternalShop.class);
		
		mkm.listTransaction().forEach(t->{
			t.getItems().forEach(item->{
				System.out.println(item.getProductName() + " " + item.getTiersAppIds());
			});
		});
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

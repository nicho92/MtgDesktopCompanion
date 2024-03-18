package org.magic.services;

import javax.annotation.Nonnull;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.abstracts.AbstractProduct;
import org.magic.api.beans.abstracts.AbstractStockItem;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGProduct;

public class ProductFactory {

	
	private ProductFactory() {
		// do nothing
	}
	
	public static AbstractStockItem<? extends AbstractProduct> generateStockItem(@Nonnull MTGProduct p)
	{
		if(p.isSealed())
			return new MTGSealedStock((MTGSealedProduct)p);
		
		return new MTGCardStock((MTGCard)p);
	}
	
	
	public static MTGProduct createDefaultProduct(EnumItems item)
	{
		if(item==EnumItems.CARD)
			return new MTGCard();
		else
			return new MTGSealedProduct();
	}
	
}

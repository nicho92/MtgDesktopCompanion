package org.magic.api.beans;

import org.magic.api.beans.abstracts.AbstractStockItem;
import org.magic.api.beans.enums.EnumCondition;

public class MTGSealedStock extends AbstractStockItem<MTGSealedProduct>  {

	private static final long serialVersionUID = 1L;

	

	public MTGSealedStock(MTGSealedProduct p)
	{
		setProduct(p);
	}

	@Override
	public void setProduct(MTGSealedProduct product) {
		this.product=product;
		setEdition(product.getEdition());
		setCondition(EnumCondition.SEALED);
	}


}

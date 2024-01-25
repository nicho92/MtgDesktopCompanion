package org.magic.api.beans;

import java.util.HashMap;

import org.magic.api.beans.abstracts.AbstractStockItem;
import org.magic.api.beans.enums.EnumItems;

public class MTGCardStock extends AbstractStockItem<MTGCard> {

	private static final long serialVersionUID = 1L;

	public MTGCardStock(MTGCard c) {
		super();
		id = -1L;
		if (c != null) {
			setProduct(c);
		}
	}

	public MTGCardStock() {
		id=-1L;
		tiersAppIds= new HashMap<>();
	}


	@Override
	public void setProduct(MTGCard c) {
		product=c;
		edition= c.getCurrentSet();
		product.setTypeProduct(EnumItems.CARD);
		product.setEdition(c.getCurrentSet());
	}

}

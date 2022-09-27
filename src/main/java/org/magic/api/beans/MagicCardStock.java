package org.magic.api.beans;

import java.util.HashMap;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.abstracts.extra.AbstractStockItem;

public class MagicCardStock extends AbstractStockItem<MagicCard> {

	private static final long serialVersionUID = 1L;

	public MagicCardStock(MagicCard c) {
		super();
		id = -1L;
		if (c != null) {
			setProduct(c);
		}
	}

	public MagicCardStock() {
		id=-1L;
		tiersAppIds= new HashMap<>();
	}


	@Override
	public void setProduct(MagicCard c) {
		product=c;
		edition= c.getCurrentSet();
		product.setTypeProduct(EnumItems.CARD);
		product.setEdition(c.getCurrentSet());
	}

}

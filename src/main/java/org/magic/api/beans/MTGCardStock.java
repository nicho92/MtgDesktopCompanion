package org.magic.api.beans;

import java.util.HashMap;

import org.magic.api.beans.abstracts.AbstractStockItem;
import org.magic.api.beans.enums.EnumFinishes;
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
		tiersAppIds= new HashMap<>();
		product=c;
		edition= c.getEdition();
		product.setTypeProduct(EnumItems.CARD);
		product.setEdition(c.getEdition());
		setFoil(c.getFinishes().size()==1 && c.getFinishes().contains(EnumFinishes.FOIL));
		setEtched(c.getFinishes().size()==1 && c.getFinishes().contains(EnumFinishes.ETCHED));
	}

}

package org.magic.api.beans;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class MagicCardStock extends AbstractStockItem<MagicCard> {

	private static final long serialVersionUID = 1L;

	public MagicCardStock(MagicCard c) {
		super();
		id = -1;
		setProduct(c);
	}
	
	public MagicCardStock() {
		id=-1;
		tiersAppIds= new HashMap<>();
	}
	

	@Override
	public void setProduct(MagicCard c) {
		product=c;
		edition= c.getCurrentSet();
		url = "https://api.scryfall.com/cards/"+(StringUtils.isEmpty(c.getScryfallId())?"/multiverse/"+c.getCurrentSet().getMultiverseid():c.getScryfallId())+"?format=image";
		product.setTypeProduct(EnumItems.CARD);
		product.setEdition(c.getCurrentSet());
	}

}

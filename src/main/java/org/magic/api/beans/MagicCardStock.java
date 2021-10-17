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
		setTypeStock(EnumItems.CARD);		
	}
	

	@Override
	public void setProduct(MagicCard c) {
		product=c;
		setProductName(c.getName());
		edition= c.getCurrentSet();
		url = "https://api.scryfall.com/cards/"+(StringUtils.isEmpty(product.getScryfallId())?"/multiverse/"+c.getCurrentSet().getMultiverseid():product.getScryfallId())+"?format=image";
		setTypeStock(EnumItems.CARD);
	}
	
	
	
	@Override
	public MagicEdition getEdition() {
		return getProduct().getCurrentSet();
	}
	
	public boolean isOversize() {
		return oversize;
	}

	public void setOversize(boolean oversize) {
		this.oversize = oversize;
	}


}

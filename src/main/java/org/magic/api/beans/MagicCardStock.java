package org.magic.api.beans;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class MagicCardStock extends AbstractStockItem<MagicCard> {

	private static final long serialVersionUID = 1L;
	private EnumCondition condition = EnumCondition.NEAR_MINT;
	private boolean foil=false;
	private boolean etched=false;
	private boolean signed=false;
	private boolean altered=false;
	private boolean oversize=false;
	

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
	
	
	public boolean isEtched() {
		return etched;
	}


	public void setEtched(boolean etched) {
		this.etched = etched;
	}



	public boolean isGrade() {
		return grade!=null;
	}


	public boolean isAltered() {
		return altered;
	}

	public void setAltered(boolean altered) {
		this.altered = altered;
	}

	public boolean isFoil() {
		return foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public EnumCondition getCondition() {
		return condition;
	}

	public void setCondition(EnumCondition condition) {
		this.condition = condition;
	}

	public boolean isOversize() {
		return oversize;
	}

	public void setOversize(boolean oversize) {
		this.oversize = oversize;
	}


}

package org.magic.api.beans;

import java.util.HashMap;

import org.magic.api.beans.abstracts.AbstractStockItem;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumItems;

public class MTGCardStock extends AbstractStockItem<MTGCard>{

	private static final long serialVersionUID = 1L;
	private boolean digital;
	
	
	
	public MTGCardStock(MTGCard c) {
		super();
		id = -1L;
		if (c != null) {
			setProduct(c);
		}
	}

	public void setDigital(boolean digitalcard) {
		this.digital = digitalcard;
	}
	
	public boolean isDigital() {
		return digital;
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
		
		setDigital(c.isOnlineOnly());
		
		
		if(c.getFinishes().size()==1 && c.getFinishes().contains(EnumFinishes.FOIL))
			setFoil(true);
		
		if(c.getFinishes().size()==1 && c.getFinishes().contains(EnumFinishes.ETCHED))
			setEtched(true);
		
	}

	@Override
	public int hashCode() {
		return (getProduct().getScryfallId()+isFoil()+isSigned()+isAltered()+isDigital()+isGrade()+isEtched()+getCondition()+getMagicCollection()+getLanguage()).hashCode();
	}
	
	
}

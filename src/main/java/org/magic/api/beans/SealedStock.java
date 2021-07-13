package org.magic.api.beans;

import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class SealedStock extends AbstractStockItem<Packaging>  {

	private static final long serialVersionUID = 1L;
	private EnumCondition condition;
	
	public SealedStock(){
		setTypeStock(EnumItems.SEALED);
		setCondition(EnumCondition.SELEAD);
	}
	
	public SealedStock(Packaging p)
	{
		setProduct(p);
	}
	
	@Override
	public void setProduct(Packaging product) {
		this.product=product;
		edition = product.getEdition();
		url = product.getUrl();
		setTypeStock(EnumItems.SEALED);
		setCondition(EnumCondition.SELEAD);
		setProductName(product.getType() +" "+  product.getEdition().getSet());
	}
	
	public EnumCondition getCondition() {
		return condition;
	}
	public void setCondition(EnumCondition condition) {
		this.condition = condition;
	}
	
	@Override
	public String toString() {
		return ""+getId();
	}
	

	
}

package org.magic.api.beans;

import org.magic.api.beans.enums.EnumStock;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class SealedStock extends AbstractStockItem<Packaging>  {

	private static final long serialVersionUID = 1L;
	private EnumStock condition = EnumStock.SELEAD;
	
	public SealedStock(){
		setTypeStock(TYPESTOCK.SEALED);
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
		setTypeStock(TYPESTOCK.SEALED);
		setProductName(product.getType() +" "+  product.getEdition().getSet());
	}
	
	public EnumStock getCondition() {
		return condition;
	}
	public void setCondition(EnumStock condition) {
		this.condition = condition;
	}
	
	@Override
	public MagicEdition getEdition() {
		return (getProduct()!=null)?getProduct().getEdition():edition;
	}
	
	@Override
	public String toString() {
		return getId()+"-"+getProduct();
	}
	

	
}

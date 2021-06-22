package org.magic.api.beans;

import org.magic.api.beans.enums.EnumStock;
import org.magic.api.interfaces.MTGShoppable;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class SealedStock extends AbstractStockItem implements MTGShoppable {

	private static final long serialVersionUID = 1L;
	private Packaging product;
	private EnumStock condition = EnumStock.SELEAD;
	
	public SealedStock(){
		
	}
	
	@Override
	public String itemName() {
		return (getProduct()!=null)?getProduct().toString():"";
	}

	@Override
	public MagicEdition getEdition() {
		return (getProduct()!=null)?getProduct().getEdition():null;
	}
	
	
	public SealedStock(Packaging p)
	{
		setProduct(p);
	}
	
	public SealedStock(Packaging p, int qte)
	{
		setProduct(p);
		setQte(qte);
	}
	
	public SealedStock(MagicEdition e, Packaging.TYPE type,String lang,Packaging.EXTRA extra, MagicCollection magicCollection)
	{
		product = new Packaging();
		product.setEdition(e);
		product.setType(type);
		product.setLang(lang);
		product.setExtra(extra);
		setProduct(product);
		setMagicCollection(magicCollection);
	}
	
	@Override
	public String toString() {
		return getId()+"-"+getProduct();
	}
	

	public Packaging getProduct() {
		return product;
	}
	public void setProduct(Packaging product) {
		this.product = product;
	}
	
	
	
	public EnumStock getCondition() {
		return condition;
	}
	public void setCondition(EnumStock condition) {
		this.condition = condition;
	}

	
}

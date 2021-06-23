package org.magic.api.beans;

import org.magic.api.beans.enums.EnumStock;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class SealedStock extends AbstractStockItem<Packaging>  {

	private static final long serialVersionUID = 1L;
	private EnumStock condition = EnumStock.SELEAD;
	
	public SealedStock(){
		
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
	
	
	public EnumStock getCondition() {
		return condition;
	}
	public void setCondition(EnumStock condition) {
		this.condition = condition;
	}
	
	@Override
	public MagicEdition getEdition() {
		return (getProduct()!=null)?getProduct().getEdition():null;
	}

	
	
	@Override
	public String toString() {
		return getId()+"-"+getProduct();
	}
	

	
}

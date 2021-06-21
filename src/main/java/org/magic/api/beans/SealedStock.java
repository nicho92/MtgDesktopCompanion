package org.magic.api.beans;

import org.magic.api.beans.enums.EnumStock;

public class SealedStock {

	private int id=-1;
	private Packaging product;
	private int qte=1;
	private String comment;
	private EnumStock condition = EnumStock.SELEAD;
	private MagicCollection collection;
	private double prices=0.0;
	
	public SealedStock()
	{
		
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
		setCollection(magicCollection);
	}
	
	public double getPrices() {
		return prices;
	}

	public void setPrices(double prices) {
		this.prices = prices;
	}

	public MagicCollection getCollection() {
		return collection;
	}

	public void setCollection(MagicCollection collection) {
		this.collection = collection;
	}

	@Override
	public String toString() {
		return getId()+"-"+getProduct();
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Packaging getProduct() {
		return product;
	}
	public void setProduct(Packaging product) {
		this.product = product;
	}
	public int getQte() {
		return qte;
	}
	public void setQte(int qte) {
		this.qte = qte;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public EnumStock getCondition() {
		return condition;
	}
	public void setCondition(EnumStock condition) {
		this.condition = condition;
	}
	
	
	
}

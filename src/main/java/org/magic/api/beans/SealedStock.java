package org.magic.api.beans;

import java.io.Serializable;

import org.magic.api.beans.enums.EnumStock;
import org.magic.api.interfaces.MTGShoppable;

public class SealedStock implements Serializable,MTGShoppable,Comparable<SealedStock> {

	private static final long serialVersionUID = 1L;
	private int id=-1;
	private Packaging product;
	private int qte=1;
	private String comment;
	private EnumStock condition = EnumStock.SELEAD;
	private MagicCollection magicCollection;
	private Double price=0.0;
	private boolean updated=false;
	
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
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public MagicCollection getMagicCollection() {
		return magicCollection;
	}

	public void setMagicCollection(MagicCollection collection) {
		this.magicCollection = collection;
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
	
	@Override
	public int getQte() {
		return qte;
	}
	
	public void setQte(int qty) {
		this.qte = qty;
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

	@Override
	public int compareTo(SealedStock o) {
		return getId()-o.getId();
		
	}

	public void setUpdated(boolean b) {
		this.updated=b;
		
	}
	
	public boolean isUpdated() {
		return updated;
	}
	
	
	
}

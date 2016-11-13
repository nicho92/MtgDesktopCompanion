package org.magic.test;

import java.util.ArrayList;
import java.util.List;

public class Want
{
	Product product;
	Double wishPrice;
	List<String> languages;
	String minCondition;
	boolean foil;
	boolean signed;
	boolean playset;
	boolean altered;
	
	
	public Want() {
		languages=new ArrayList<String>();
	}
	
	public Double getWishPrice() {
		return wishPrice;
	}
	public void setWishPrice(Double wishPrice) {
		this.wishPrice = wishPrice;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public String getMinCondition() {
		return minCondition;
	}
	public void setMinCondition(String minCondition) {
		this.minCondition = minCondition;
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
	public boolean isPlayset() {
		return playset;
	}
	public void setPlayset(boolean playset) {
		this.playset = playset;
	}
	public boolean isAltered() {
		return altered;
	}
	public void setAltered(boolean altered) {
		this.altered = altered;
	}

	int qte;

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product idProduct) {
		this.product = idProduct;
	}
	public int getQte() {
		return qte;
	}
	public void setQte(int qte) {
		this.qte = qte;
	}
	
	@Override
	public String toString() {
		return getProduct() +" (" + getQte() +")";
	}
}
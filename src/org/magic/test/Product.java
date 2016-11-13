package org.magic.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCardNames;

public class Product
{
	String idProduct;
	String name;
	List<MagicCardNames> names;
	URL webSite;
	String expension;
	String rarity;
	String number;
	int idSet;
	
	public Product() {
		names = new ArrayList<MagicCardNames>();
	}
	
	
	public String getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<MagicCardNames> getNames() {
		return names;
	}
	public void setNames(List<MagicCardNames> names) {
		this.names = names;
	}
	public URL getWebSite() {
		return webSite;
	}
	public void setWebSite(URL webSite) {
		this.webSite = webSite;
	}
	public String getExpension() {
		return expension;
	}
	public void setExpension(String expension) {
		this.expension = expension;
	}
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getIdSet() {
		return idSet;
	}
	public void setIdSet(int idSet) {
		this.idSet = idSet;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
	
}
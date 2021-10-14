package org.magic.api.beans.shop;

import java.io.Serializable;

import org.magic.api.beans.MagicEdition;

public class Product implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private  int id;
	private String url;
	private String productName;
	private MagicEdition edition;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public MagicEdition getEdition() {
		return edition;
	}
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	
	
	
}

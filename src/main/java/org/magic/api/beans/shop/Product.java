package org.magic.api.beans.shop;

import java.io.Serializable;

import org.magic.api.beans.MagicEdition;

public abstract class Product implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String productId;
	protected String url;
	protected String name;
	protected MagicEdition edition;
	
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String id) {
		this.productId = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public MagicEdition getEdition() {
		return edition;
	}
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}

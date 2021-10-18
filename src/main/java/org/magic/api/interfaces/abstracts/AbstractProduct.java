package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGProduct;

public abstract class AbstractProduct implements  MTGProduct {

	private static final long serialVersionUID = 1L;
	
	protected String productId;
	protected String url;
	protected String name;
	protected MagicEdition edition;
	protected EnumItems typeProduct;
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public EnumItems getTypeProduct() {
		return typeProduct;
	}
	
	@Override
	public void setTypeProduct(EnumItems type) {
		this.typeProduct=type;
	}
	
	@Override
	public String getProductId() {
		return productId;
	}
	@Override
	public void setProductId(String id) {
		this.productId = id;
	}
	@Override
	public String getUrl() {
		return url;
	}
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public MagicEdition getEdition() {
		return edition;
	}
	@Override
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
}

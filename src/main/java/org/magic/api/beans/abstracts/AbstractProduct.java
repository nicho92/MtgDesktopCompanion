package org.magic.api.beans.abstracts;

import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.interfaces.MTGProduct;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractProduct implements MTGProduct{

	private static final long serialVersionUID = 1L;

	protected Long productId;
	@SerializedName(alternate = "imageName", value = "url") protected String url;
	protected String name;
	protected MTGEdition edition;
	protected EnumItems typeProduct;
	protected Category category;


	@Override
	public String getStoreId() {
		return toString();
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public void setCategory(Category c) {
		this.category= c;

	}

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
	public Long getProductId() {
		return productId;
	}
	@Override
	public void setProductId(Long id) {
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
	public MTGEdition getEdition() {
		return edition;
	}
	@Override
	public void setEdition(MTGEdition edition) {
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

	
	public static MTGProduct createDefaultProduct()
	{
		
		return new AbstractProduct() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public String toString() {
				return getName();
			}
		};
	}


}

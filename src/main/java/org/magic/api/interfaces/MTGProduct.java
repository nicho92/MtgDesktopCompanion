package org.magic.api.interfaces;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;

public interface MTGProduct extends MTGStorable {

	public Long getProductId();

	public void setProductId(Long id);

	public String getUrl();

	public void setUrl(String url);

	public MagicEdition getEdition();

	public void setEdition(MagicEdition edition);

	public String getName();

	public void setName(String name);

	public void setTypeProduct(EnumItems type);

	public EnumItems getTypeProduct();

	public void setCategory(Category c);

	public Category getCategory();
	
	default boolean isSealed()
	{
		return getTypeProduct()!=EnumItems.CARD;
	}
	
	
	
}
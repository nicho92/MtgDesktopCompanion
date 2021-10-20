package org.magic.api.interfaces;

import java.io.Serializable;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;

public interface MTGProduct extends Serializable {

	public Integer getProductId();

	public void setProductId(Integer id);

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
	
}
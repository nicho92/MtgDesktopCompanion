package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGShoppable;

public interface MTGStockItem<T extends MTGShoppable> {

	void setProduct(T product);

	String getProductName();

	void setProductName(String productName);

	MagicEdition getEdition();

	void setTypeStock(EnumItems typeStock);

	EnumItems getTypeStock();

	T getProduct();

	void setGrade(Grading grade);

	Grading getGrade();

	String toString();

	Double getPrice();

	void setPrice(Double price);

	Integer getId();

	void setId(Integer id);

	MagicCollection getMagicCollection();

	void setMagicCollection(MagicCollection magicCollection);

	Integer getQte();

	void setQte(Integer qte);

	String getComment();

	void setComment(String comment);

	String getLanguage();

	void setLanguage(String language);

	int compareTo(MTGStockItem<T> o);

	int hashCode();

	boolean isUpdated();

	void setUpdated(boolean updated);

	boolean equals(Object obj);

}
package org.magic.api.interfaces;

import java.io.Serializable;
import java.util.Map;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;

public interface MTGStockItem extends Serializable, Comparable<MTGStockItem> {

	String getProductName();

	void setProductName(String productName);

	MagicEdition getEdition();

	void setTypeStock(EnumItems typeStock);

	EnumItems getTypeStock();

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

	int hashCode();

	boolean isUpdated();

	void setUpdated(boolean updated);

	boolean equals(Object obj);

	String getTiersAppIds(String name);

	void setTiersAppIds(Map<String, String> tiersAppIds);

	Map<String, String> getTiersAppIds();
	
	public String getUrl();
	

}
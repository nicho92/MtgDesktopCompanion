package org.magic.api.interfaces;

import java.util.Map;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.enums.EnumCondition;

public interface MTGStockItem extends MTGStorable, Comparable<MTGStockItem> {
	
	public 	void setGrade(Grading grade);

	public 	Grading getGrade();

	public 	String toString();

	public 	Double getPrice();

	public 	void setPrice(Double price);

	public 	Long getId();
	
	public 	void setId(Integer id);
	
	public 	void setId(Long id);

	public 	MagicCollection getMagicCollection();

	public 	void setMagicCollection(MagicCollection magicCollection);

	public 	Integer getQte();

	public 	void setQte(Integer qte);

	public 	String getComment();

	public 	void setComment(String comment);

	public 	String getLanguage();

	public 	void setLanguage(String language);

	public 	boolean isUpdated();

	public 	void setUpdated(boolean updated);

	public 	String getTiersAppIds(String name);

	public 	void setTiersAppIds(Map<String, String> tiersAppIds);

	public 	Map<String, String> getTiersAppIds();
	
	public String getUrl();

	public 	boolean isEtched();

	public 	void setEtched(boolean etched);

	public 	boolean isGrade();

	public 	boolean isAltered();

	public 	void setAltered(boolean altered);

	public 	boolean isSigned();

	public 	void setFoil(boolean foil);

	public 	void setSigned(boolean signed);

	public 	boolean isFoil();

	public <T extends MTGProduct> T getProduct();

	public EnumCondition getCondition();
	
	public void setCondition(EnumCondition condtion);

}
package org.magic.api.interfaces;

import java.util.Map;

import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.api.interfaces.extra.MTGSerializable;

public interface MTGStockItem extends MTGSerializable, Comparable<MTGStockItem> {


	public void setGrade(MTGGrading grade);

	public MTGGrading getGrade();

	public void setPrice(Double price);
	
	public MoneyValue getValue();
	
	public Long getId();

	public void setId(Integer id);

	public void setId(Long id);

	public MTGCollection getMagicCollection();

	public void setMagicCollection(MTGCollection magicCollection);

	public Integer getQte();

	public void setQte(Integer qte);

	public String getComment();

	public void setComment(String comment);

	public String getLanguage();

	public void setLanguage(String language);

	public boolean isUpdated();

	public void setUpdated(boolean updated);

	public String getTiersAppIds(String name);

	public void setTiersAppIds(Map<String, String> tiersAppIds);

	public Map<String, String> getTiersAppIds();

	public boolean isEtched();

	public void setEtched(boolean etched);

	public boolean isGrade();

	public boolean isAltered();

	public void setAltered(boolean altered);

	public boolean isSigned();

	public void setFoil(boolean foil);

	public void setSigned(boolean signed);

	public boolean isFoil();

	public <T extends MTGProduct> T getProduct();

	public EnumCondition getCondition();

	public void setCondition(EnumCondition condtion);

}
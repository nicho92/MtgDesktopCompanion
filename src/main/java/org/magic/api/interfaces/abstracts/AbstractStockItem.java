package org.magic.api.interfaces.abstracts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGStockItem;

public abstract class AbstractStockItem<T extends Serializable> implements MTGStockItem {
	
	protected static final long serialVersionUID = 1L;
	protected Integer id=-1;
	protected MagicCollection magicCollection;
	protected Integer qte=1;
	protected String comment;
	protected String language="English";
	protected boolean updated=false;
	protected Double price=0.0;
	protected Grading grade;
	protected String productName;
	protected MagicEdition edition;
	protected T product;
	protected EnumItems typeStock;
	protected String url;
	protected Map<String,String> tiersAppIds;
	
	protected AbstractStockItem() {
		tiersAppIds = new HashMap<>();
	}

	@Override
	public String getTiersAppIds(String name) {
		return tiersAppIds.get(name);
	}
	
	@Override
	public Map<String, String> getTiersAppIds() {
		return tiersAppIds;
	}

	@Override
	public void setTiersAppIds(Map<String, String> tiersAppIds) {
		this.tiersAppIds = tiersAppIds;
	}
	
	public abstract void setProduct(T product);
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public MagicEdition getEdition()
	{
		return edition;
	}
	
	public void setTypeStock(EnumItems typeStock) {
		this.typeStock = typeStock;
	}
	
	public EnumItems getTypeStock() {
		return typeStock;
	}
	
	
	public T getProduct() {
		return product;
	}
	
	
	
	@Override
	public void setGrade(Grading grade) {
		this.grade = grade;
	}


	@Override
	public Grading getGrade() {
		return grade;
	}

	@Override
	public String toString() {
		return String.valueOf(getId());
	}
	
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MagicCollection getMagicCollection() {
		return magicCollection;
	}

	public void setMagicCollection(MagicCollection magicCollection) {
		this.magicCollection = magicCollection;
	}

	public Integer getQte() {
		return qte;
	}

	public void setQte(Integer qte) {
		this.qte = qte;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean isUpdated() {
		return updated;
	}
	
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	@Override
	public int compareTo(MTGStockItem o) {
		return getId()-o.getId();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractStockItem))
			return false;
		
		return getId() == ((AbstractStockItem<?>)obj).getId();
	}


	
}

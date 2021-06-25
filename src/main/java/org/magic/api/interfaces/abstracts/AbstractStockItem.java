package org.magic.api.interfaces.abstracts;

import java.io.Serializable;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;

public abstract class AbstractStockItem<T extends Serializable> implements Serializable, Comparable<AbstractStockItem<T>> {
	public enum TYPESTOCK  { SEALED, CARD,BOX,BOOSTER,FULLSET,LOTS,BUNDLE}

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
	protected TYPESTOCK typeStock;
	protected String url;
	
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
	
	
	public void setTypeStock(TYPESTOCK typeStock) {
		this.typeStock = typeStock;
	}
	
	public TYPESTOCK getTypeStock() {
		return typeStock;
	}
	
	
	public T getProduct() {
		return product;
	}
	
	
	
	
	public void setGrade(Grading grade) {
		this.grade = grade;
	}

	
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
	public int compareTo(AbstractStockItem<T> o) {
		return getId()-o.getId();
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
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractStockItem))
			return false;
		
		return getId() == ((AbstractStockItem<?>)obj).getId();
	}


	
}

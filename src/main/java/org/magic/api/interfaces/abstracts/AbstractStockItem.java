package org.magic.api.interfaces.abstracts;

import java.io.Serializable;

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGShoppable;

public abstract class AbstractStockItem implements MTGShoppable, Serializable, Comparable<AbstractStockItem> {

	protected static final long serialVersionUID = 1L;
	protected Integer id=-1;
	protected MagicCollection magicCollection;
	protected Integer qte=1;
	protected String comment;
	protected String language="English";
	protected boolean updated=false;
	protected Double price;
	
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
	public int compareTo(AbstractStockItem o) {
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
		if(!(obj instanceof MTGShoppable))
			return false;
		
		return getId() == ((MTGShoppable)obj).getId();
	}


	
}

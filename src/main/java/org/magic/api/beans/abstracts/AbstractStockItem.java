package org.magic.api.beans.abstracts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.MTGStockItem;

public abstract class AbstractStockItem<T extends MTGProduct> implements MTGStockItem {

	protected static final long serialVersionUID = 1L;
	protected Long id=-1L;
	protected MTGCollection magicCollection;
	protected Integer qte=1;
	protected String comment="";
	protected String language="English";
	protected boolean updated=false;
	protected Double price=0.0;
	protected MTGGrading grade;
	protected MTGEdition edition;
	protected T product;
	protected transient Map<String,String> tiersAppIds;
	protected boolean foil=false;
	protected boolean etched=false;
	protected boolean signed=false;
	protected boolean altered=false;
	protected boolean oversize=false;
	protected Date dateUpdate;
	protected String sku;

	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	protected EnumCondition condition = EnumCondition.NEAR_MINT;

	@SuppressWarnings("unchecked")
	@Override
	public T getProduct() {
		return product;
	}

	@Override
	public String getStoreId() {
		return String.valueOf(getId());
	}

	public void setProduct(T product)
	{
		this.product = product;
	}

	@Override
	public EnumCondition getCondition() {
		return condition;
	}
	@Override
	public void setCondition(EnumCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean isEtched() {
		return etched;
	}


	@Override
	public void setEtched(boolean etched) {
		this.etched = etched;
	}

	@Override
	public boolean isGrade() {
		return grade!=null;
	}


	@Override
	public boolean isAltered() {
		return altered;
	}

	@Override
	public void setAltered(boolean altered) {
		this.altered = altered;
	}

	@Override
	public boolean isFoil() {
		return foil;
	}

	@Override
	public void setFoil(boolean foil) {
		this.foil = foil;
	}

	@Override
	public boolean isSigned() {
		return signed;
	}

	@Override
	public void setSigned(boolean signed) {
		this.signed = signed;
	}

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

	@Override
	public void setGrade(MTGGrading grade) {
		this.grade = grade;
	}


	@Override
	public MTGGrading getGrade() {
		return grade;
	}

	@Override
	public String toString() {
		return String.valueOf(getId());
	}

	@Override
	public Double getPrice() {
		return price;
	}
	@Override
	public void setPrice(Double price) {
		this.price = price;
	}
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id.longValue();
	}
	@Override
	public MTGCollection getMagicCollection() {
		return magicCollection;
	}
	@Override
	public void setMagicCollection(MTGCollection magicCollection) {
		this.magicCollection = magicCollection;
	}
	@Override
	public Integer getQte() {
		return qte;
	}
	@Override
	public void setQte(Integer qte) {
		this.qte = qte;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String getLanguage() {
		return language;
	}
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public int hashCode() {
		return getId().intValue();
	}

	@Override
	public boolean isUpdated() {
		return updated;
	}
	@Override
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	@Override
	public int compareTo(MTGStockItem o) {
		return (int) (getId()-o.getId());
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MTGStockItem))
			return false;

		return getId() == ((MTGStockItem)obj).getId();
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSku() {
		return sku;
	}
}



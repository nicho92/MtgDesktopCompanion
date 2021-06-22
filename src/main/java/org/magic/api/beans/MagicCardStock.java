package org.magic.api.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGShoppable;

public class MagicCardStock implements Serializable, Comparable<MagicCardStock>, MTGShoppable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private MagicCard magicCard;
	private MagicCollection magicCollection;
	private int qte=1;
	private String language="English";
	private EnumCondition condition = EnumCondition.NEAR_MINT;
	private String comment;
	private boolean foil=false;
	private boolean etched=false;
	private boolean signed=false;
	private boolean altered=false;
	private boolean update;
	private double price=0.0;
	private boolean oversize=false;
	private Grading grade;
	private Map<String,String> tiersAppIds;
	

	@Override
	public String itemName() {
		return getMagicCard().toString();
	}


	@Override
	public MagicEdition getEdition() {
		return getMagicCard().getCurrentSet();
	}
	
	
	
	public boolean isEtched() {
		return etched;
	}


	public void setEtched(boolean etched) {
		this.etched = etched;
	}


	public String getTiersAppIds(String name) {
		return tiersAppIds.get(name);
	}
	
	
	public Map<String, String> getTiersAppIds() {
		return tiersAppIds;
	}

	public void setTiersAppIds(Map<String, String> tiersAppIds) {
		this.tiersAppIds = tiersAppIds;
	}

	public boolean isGrade() {
		return grade!=null;
	}

	public void setGrade(Grading grade) {
		this.grade = grade;
	}

	
	public Grading getGrade() {
		return grade;
	}
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public MagicCardStock() {
		id = -1;
		tiersAppIds = new HashMap<>();
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean isAltered() {
		return altered;
	}

	public void setAltered(boolean altered) {
		this.altered = altered;
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer idstock) {
		this.id = idstock;
	}

	public MagicCollection getMagicCollection() {
		return magicCollection;
	}

	public void setMagicCollection(MagicCollection magicCollection) {
		this.magicCollection = magicCollection;
	}

	public boolean isFoil() {
		return foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public MagicCard getMagicCard() {
		return magicCard;
	}

	public void setMagicCard(MagicCard magicCard) {
		this.magicCard = magicCard;
	}

	public int getQte() {
		return qte;
	}

	public void setQte(int qte) {
		this.qte = qte;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public EnumCondition getCondition() {
		return condition;
	}

	public void setCondition(EnumCondition condition) {
		this.condition = condition;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isOversize() {
		return oversize;
	}

	public void setOversize(boolean oversize) {
		this.oversize = oversize;
	}

	@Override
	public int compareTo(MagicCardStock o) {
		return getId()-o.getId();
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MagicCardStock))
			return false;
		
		return getId() == ((MagicCardStock)obj).getId();
	}



}

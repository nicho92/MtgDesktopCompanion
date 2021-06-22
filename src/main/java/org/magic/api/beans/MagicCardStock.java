package org.magic.api.beans;

import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGShoppable;
import org.magic.api.interfaces.abstracts.AbstractStockItem;

public class MagicCardStock extends AbstractStockItem{

	private static final long serialVersionUID = 1L;
	private MagicCard magicCard;
	private EnumCondition condition = EnumCondition.NEAR_MINT;
	private boolean foil=false;
	private boolean etched=false;
	private boolean signed=false;
	private boolean altered=false;
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

	public MagicCardStock() {
		id = -1;
		tiersAppIds = new HashMap<>();
	}

	public boolean isAltered() {
		return altered;
	}

	public void setAltered(boolean altered) {
		this.altered = altered;
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

	public EnumCondition getCondition() {
		return condition;
	}

	public void setCondition(EnumCondition condition) {
		this.condition = condition;
	}

	public boolean isOversize() {
		return oversize;
	}

	public void setOversize(boolean oversize) {
		this.oversize = oversize;
	}

}

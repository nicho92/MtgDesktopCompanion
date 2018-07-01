package org.magic.api.beans;

public class MagicCardStock {

	private int idstock;
	private MagicCard magicCard;
	private MagicCollection magicCollection;
	private int qte;
	private String language;
	private EnumCondition condition;
	private String comment;
	private boolean foil;
	private boolean signed;
	private boolean altered;
	private boolean update;
	private double price;
	private boolean oversize;
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public MagicCardStock() {
		idstock = -1;
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
		return String.valueOf(idstock);
	}

	public int getIdstock() {
		return idstock;
	}

	public void setIdstock(int idstock) {
		this.idstock = idstock;
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

}

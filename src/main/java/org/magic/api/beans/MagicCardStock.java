package org.magic.api.beans;

import java.io.Serializable;

public class MagicCardStock implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idstock;
	private MagicCard magicCard;
	private MagicCollection magicCollection;
	private int qte=1;
	private String language="English";
	private EnumCondition condition = EnumCondition.NEAR_MINT;
	private String comment;
	private boolean foil=false;
	private boolean signed=false;
	private boolean altered=false;
	private boolean update;
	private double price=0.0;
	private boolean oversize=false;
	private boolean grade=false;
	private Double gradeNote=0.0;
	private String gradeName;
	
	
	
	
	
	public boolean isGrade() {
		return grade;
	}

	public void setGrade(boolean grade) {
		this.grade = grade;
	}

	public Double getGradeNote() {
		return gradeNote;
	}

	public void setGradeNote(Double gradeNote) {
		this.gradeNote = gradeNote;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

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

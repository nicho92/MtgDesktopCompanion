package org.magic.api.beans;

import javax.swing.AbstractAction;

public class MTGKeyWord {

	public static enum TYPE {Evergreen,Actions,Expert,Ability,Discontinued};

	
	private String keyword;
	private String description;
	private TYPE type;
	private String ability;
	
	
	public String getAbility() {
		return ability;
	}

	public void setAbility(String ability) {
		this.ability = ability;
	}

	public MTGKeyWord() {
		// TODO Auto-generated constructor stub
	}
	
	public MTGKeyWord(String k,TYPE t,String description)
	{
		this.keyword=k;
		this.type=t;
		this.description=description;
	}
	
	
	
	public MTGKeyWord(String keyword2) {
		this.keyword=keyword2;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/*
	@Override
	public boolean equals(Object obj) {
		KeyWord k = (KeyWord)obj;
		
		return k.getKeyword().toLowerCase().equals(getKeyword().toLowerCase());
	}
	
	@Override
	public int hashCode() {
		return getKeyword().hashCode();
	}*/
	
	@Override
	public String toString() {
		return getKeyword();
	}
	
		
}

package org.magic.api.beans;

public class MTGKeyWord {

	public static enum TYPE { Abilities, Action,Word};
	public static enum SUBTYPE {Evergreen,Other};
	//public static enum action {spell,statik,activated,triggered};
	
	private String keyword;
	private String description;
	
	private TYPE type;
	private SUBTYPE subtype;
	private String action;
	
	public MTGKeyWord() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return getKeyword();
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

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public SUBTYPE getSubtype() {
		return subtype;
	}

	public void setSubtype(SUBTYPE subtype) {
		this.subtype = subtype;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
	
		
}

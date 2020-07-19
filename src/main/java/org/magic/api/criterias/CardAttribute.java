package org.magic.api.criterias;

import java.io.Serializable;

public class CardAttribute implements Serializable, Comparable<CardAttribute>{
	
	private static final long serialVersionUID = 1L;
	public enum TYPE_FIELD {INTEGER,FLOAT,STRING,BOOLEAN}
	private String name;
	private TYPE_FIELD type;
	
	public CardAttribute(String name, TYPE_FIELD type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TYPE_FIELD getType() {
		return type;
	}
	public void setType(TYPE_FIELD type) {
		this.type = type;
	}
	
	 @Override
	public String toString() {
		return getName();
				
	}

	@Override
	public int compareTo(CardAttribute o) {
		return (getName().compareTo(String.valueOf(o)));
	}
	
}

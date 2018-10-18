package org.magic.api.beans;

import java.io.Serializable;

public class MagicCardNames implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	private String name;
	private String text;
	private String type;
	
	private int gathererId;

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGathererId() {
		return gathererId;
	}

	public void setGathererId(int gathererId) {
		this.gathererId = gathererId;
	}

	@Override
	public String toString() {
		return getLanguage();
	}
}

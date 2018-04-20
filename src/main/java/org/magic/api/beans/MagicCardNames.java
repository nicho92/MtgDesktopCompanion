package org.magic.api.beans;

import java.io.Serializable;

public class MagicCardNames implements Serializable {

	private String language;
	private String name;
	private int gathererId;

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

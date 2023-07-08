package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

public enum EnumExtra {
	SET ("set"), 
	DRAFT("default"), 
	COLLECTOR ("collector"),
	THEME("default"),
	GIFT ("default"),
	VIP ("vip"),
	WELCOME("default"),	
	JUMP ("jumpstart"),
	INTRO("default"),
	PLANESWALKER("default"),
	STARTER("starter"),
	BRAWL("default");
	
	private String mtgjsonname;

	@Override
	public String toString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
	
	private EnumExtra(String mtgjsonName) {
		this.mtgjsonname=mtgjsonName;
	}
	
	public String getMtgjsonname() {
		return mtgjsonname;
	}
}

package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

public enum EnumExtra {
	BEYOND("beyond"),
	BRAWL("brawl"),
	COLLECTOR ("collector"), 
	DRAFT("draft"),
	GIFT ("gift-bundle-promo"),
	FATPACK ("fat-pack"),
	INTRO ("intro"),
	JUMP ("jumpstart"),
	PLANESWALKER("default"),	
	PLAY("play"),
	SET ("set"),
	STARTER("starter"),
	THEME("default"),
	VIP ("vip"),
	WELCOME("welcome");
	
	private String mtgjsonname;

	private EnumExtra(String mtgjsonName) {
		this.mtgjsonname=mtgjsonName;
	}
	
	public String getMtgjsonname() {
		return mtgjsonname;
	}
	
	@Override
	public String toString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}

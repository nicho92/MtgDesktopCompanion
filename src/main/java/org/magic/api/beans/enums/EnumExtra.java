package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.interfaces.MTGEnumeration;

public enum EnumExtra implements MTGEnumeration{

	ARENA("arena"),
	BEYOND("beyond"),
	BOXTOPPER("box-topper"),
	BRAWL("brawl"),
	COLLECTOR ("collector"), 
	COLLECTOR_SAMPLE("collector-sample"),
	DRAFT("draft"),
	GIFT ("gift-bundle-promo"),
	FATPACK ("fat-pack"),
	INTRO ("intro"),
	JUMP ("jumpstart"),
	PLANESWALKER("default"),	
	PLAY("play"),
	PREMIUM("premium"),
	PRERELEASE("prerelease"),
	SET ("set"),
	SIX("six"),
	STARTER("starter"),
	THEME("default"),
	THEME_W("theme-w"),
	THEME_U("theme-u"),
	THEME_B("theme-b"),
	THEME_R("theme-r"),
	THEME_G("theme-g"),
	TOURNAMENT("tournament"),
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
	
	public static EnumExtra parseByLabel(String s)
	{
		for(var e : EnumExtra.values())
		{
			if(e.getMtgjsonname().equalsIgnoreCase(s))
				return e;
		}
		logger.warn("No extra found for {}", s);
		
		return null;
		
	}
	
	
}

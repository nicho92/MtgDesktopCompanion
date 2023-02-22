package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

public enum EnumExtra {
	SET, DRAFT, COLLECTOR,THEME,GIFT,VIP,WELCOME,JUMP,INTRO,PLANESWALKER,BRAWL;
	

	public String toString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}

package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

public enum EnumCondition {

	MINT ("M"), 
	NEAR_MINT ("NM"), 
	EXCELLENT("EX"), 
	GOOD("GD"), 
	LIGHTLY_PLAYED("LP"), 
	PLAYED("PL"), 
	POOR ("PR"),
	PROXY("PX"), 
	OVERSIZED("OV"),
	ONLINE ("OL"),
	SEALED ("SD"), 
	OPENED ("OP"), 
	DAMAGED ("DM");

	
	private String codename;
	private String label;
	
	
	private EnumCondition(String codename) {
		this.codename=codename;
		label = StringUtils.capitalize(name().replace("_", " ").toLowerCase());
	}
	
	public String getCodename() {
		return codename;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
}

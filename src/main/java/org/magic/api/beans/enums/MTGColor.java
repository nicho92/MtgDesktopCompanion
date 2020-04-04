package org.magic.api.beans.enums;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

public enum MTGColor {
	
	WHITE ("W",Color.WHITE),
	BLUE ("U",Color.BLUE),
	BLACK ("B",Color.BLACK),
	RED ("R",Color.RED),
	GREEN ("G",Color.GREEN),
	UNCOLOR ("C",Color.GRAY),
	GOLD ("G",Color.YELLOW);
	
	
	private String pattern;
	private Color color;
	
	private MTGColor(String s,Color c) {
		pattern=s;
		color=c;
	}
	
	public Color toColor()
	{
		return color;
	}
	
	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
	
	public String toLetter() {
		return pattern.toLowerCase();
	}
	
	public String toStandardCode() {
		return "{"+toLetter()+"}";
	}
}

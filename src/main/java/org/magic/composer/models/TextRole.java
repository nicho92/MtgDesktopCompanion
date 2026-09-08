package org.magic.composer.models;

import java.awt.Font;

public enum TextRole {

	TITLE (24,"Beleren Bold",Font.PLAIN),
    TYPE_LINE (20,"Beleren Bold",Font.PLAIN),
    TEXT (18,"MatrixBold",Font.PLAIN),
    FLAVOR (16,"MatrixBold",Font.ITALIC),
    POWER_TOUGHNESS(20,"Beleren Bold",Font.PLAIN),
	
	ARTIST (8,"Beleren Small Caps",Font.PLAIN),
	COPYRIGHT (8,"MPlantin",Font.PLAIN),
	COLLECTOR_INFO (8,"GothamPro-Medium",Font.PLAIN);
	
	private int size;
	private String fontname;
	private int style;

	
	TextRole(int size, String fontname, int style) {
		this.size = size;
		this.fontname = fontname;
		this.style = style;
	}
	
	public Font getFont() {
		return new Font(fontname, style, size);
	}
		
}

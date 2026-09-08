package org.magic.composer.models;

import java.awt.Font;

public enum TextRole {

	TITLE (24,"Beleren",Font.PLAIN),
    TYPE_LINE (20,"Beleren",Font.PLAIN),
    RULES (18,"Matrix Book",Font.PLAIN),
    FLAVOR (19,"Matrix Book Italic",Font.ITALIC),
    POWER_TOUGHNESS(20,"Beleren",Font.PLAIN);

	TextRole(int size, String fontname, int style) {
		// TODO Auto-generated constructor stub
	}
	
	
}

package org.magic.composer.models;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;

public enum TextRole {

	TITLE (124,"Beleren Bold",SwingConstants.LEFT, Font.PLAIN),
	TYPE_LINE (120,"Beleren Bold",SwingConstants.LEFT,Font.PLAIN),
	TEXT (118,"MatrixBold",SwingConstants.LEFT,Font.PLAIN),
	FLAVOR (116,"MatrixBold",SwingConstants.LEFT,Font.ITALIC),
	POWER_TOUGHNESS(120,"Beleren Bold",SwingConstants.CENTER,Font.PLAIN),
	
	ARTIST (118,"Beleren Small Caps",SwingConstants.LEFT,Font.PLAIN,Color.WHITE),
	COPYRIGHT (118,"MPlantin",SwingConstants.RIGHT,Font.PLAIN,Color.WHITE),
	COLLECTOR_INFO (118,"GothamPro-Medium",SwingConstants.LEFT,Font.PLAIN,Color.WHITE),
	SET_AND_LANG (118,"GothamPro-Medium",SwingConstants.LEFT,Font.PLAIN,Color.WHITE),
    	SEPARATOR(118,"NDPMTG",SwingConstants.CENTER,Font.PLAIN,Color.WHITE);
    	
    
	private int size;
	private String fontname;
	private int style;
	private Color color;
	private int alignement;
	
	
	TextRole(int size, String fontname, int alignement,int style) {
		this.size = size;
		this.fontname = fontname;
		this.style = style;
		this.alignement=alignement;
		color = Color.BLACK;
	}
	
	TextRole(int size, String fontname, int alignement,int style,Color c) {
		this(size,fontname,alignement,style);
		color = c;
	}
	
	
	public Color getColor() {
	    return color;
	}
	
	public int getAlignement() {
	    return alignement;
	}
	
	public Font getFont() {
		return new Font(fontname, style, size);
	}
		
}

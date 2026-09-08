package org.magic.composer.models;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;

public enum TextRole {

	TITLE (24,"Beleren Bold",SwingConstants.LEFT, Font.PLAIN),
	TYPE_LINE (20,"Beleren Bold",SwingConstants.LEFT,Font.PLAIN),
	TEXT (18,"MatrixBold",SwingConstants.LEFT,Font.PLAIN),
	FLAVOR (16,"MatrixBold",SwingConstants.LEFT,Font.ITALIC),
	POWER_TOUGHNESS(20,"Beleren Bold",SwingConstants.CENTER,Font.PLAIN),
	
	ARTIST (8,"Beleren Small Caps",Font.PLAIN,SwingConstants.LEFT,Color.WHITE),
	COPYRIGHT (8,"MPlantin",Font.PLAIN, SwingConstants.RIGHT,Color.WHITE),
	COLLECTOR_INFO (8,"GothamPro-Medium",SwingConstants.LEFT,Font.PLAIN,Color.WHITE),
    	SET_AND_LANG (8,"GothamPro-Medium",SwingConstants.LEFT,Font.PLAIN,Color.WHITE),
    	SEPARATOR(8,"NDPMTG",Font.PLAIN,SwingConstants.CENTER,Color.WHITE);
    	
    
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

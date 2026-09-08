package org.magic.composer.models;

import java.awt.Color;

public enum BorderColor {

	BLACK(Color.BLACK),
	WHITE(Color.WHITE),
	SILVER(Color.LIGHT_GRAY),
	YELLOW(new Color(186, 142, 35)),
	CUSTOM(Color.BLUE);

	private Color color;

	BorderColor(Color c) {
		this.color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	
}

package org.magic.api.beans.enums;

import java.awt.Color;

public enum EnumPlayerStatus {

	ONLINE("Online",new Color(76,181,108)), 
	BUSY("Buzy",Color.RED), 
	AWAY("Away",Color.ORANGE), 
	GAMING("Gaming",Color.CYAN),
	DISCONNECTED("Disconnected",Color.BLACK),
	CONNECTED("Connected",Color.BLACK);
	
	private String libelle;
	private Color color;

	
	public Color getColor() {
		return color;
	}
	
	public String getLibelle() {
		return libelle;
	}
	
	private EnumPlayerStatus(String l, Color c)
	{
		this.libelle=l;
		this.color = c;
	}
	
	@Override
	public String toString() {
		return getLibelle();
	}
}

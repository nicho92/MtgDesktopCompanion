package org.magic.api.beans.enums;

import java.awt.Color;

import javax.swing.Icon;

import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.tools.UITools;

public enum EnumPlayerStatus implements MTGIconable {

	ONLINE("Online",new Color(76,181,108)), 
	BUSY("Buzy",Color.RED), 
	AWAY("Away",Color.ORANGE), 
	GAMING("Gaming",Color.CYAN),
	DISCONNECTED("Disconnected",Color.GRAY),
	CONNECTED("Connected",Color.BLACK);
	
	private String name;
	private Color color;

	
	public Color getColor() {
		return color;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	private EnumPlayerStatus(String l, Color c)
	{
		this.name=l;
		this.color = c;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Icon getIcon() {
		return UITools.generateColoredIcon(getColor());
	}
}

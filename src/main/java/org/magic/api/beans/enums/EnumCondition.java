package org.magic.api.beans.enums;

import java.awt.Color;

import javax.swing.Icon;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.tools.UITools;

public enum EnumCondition implements MTGIconable{

	MINT ("M", new Color(34,249,26) ), 
	NEAR_MINT ("NM",new Color(36,210,30)), 
	EXCELLENT("EX",new Color(159,233,84)), 
	GOOD("GD",new Color(204,204,0)), 
	LIGHTLY_PLAYED("LP",new Color(169,169,66)), 
	PLAYED("PL",new Color(102,102,0)), 
	POOR ("PR",new Color(204,102,0)),
	PROXY("PX",new Color(255,102,255)), 
	DAMAGED ("DM",new Color(153,76,0)),
	SEALED ("SD",new Color(0,153,153)),
	OPENED ("OP",new Color(0,102,102));

	
	private String codename;
	private String label;
	private Color color;
	
	private EnumCondition(String codename, Color c) {
		this.codename=codename;
		this.color = c;
		label = StringUtils.capitalize(name().replace("_", " ").toLowerCase());
		
	}
	
	public String getCodename() {
		return codename;
	}
	
	public String getName() {
		return label;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override	
	public Icon getIcon()
	{
			return UITools.generateRoundedIcon(color);
	}
	
	
	
}

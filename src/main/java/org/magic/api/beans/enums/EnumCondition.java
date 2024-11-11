package org.magic.api.beans.enums;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.magic.services.MTGConstants;

public enum EnumCondition {

	MINT ("M", new Color(34,249,26) ), 
	NEAR_MINT ("NM",new Color(36,210,30)), 
	EXCELLENT("EX",new Color(159,233,84)), 
	GOOD("GD",new Color(204,204,0)), 
	LIGHTLY_PLAYED("LP",new Color(204,204,84)), 
	PLAYED("PL",new Color(102,102,0)), 
	POOR ("PR",new Color(204,102,0)),
	PROXY("PX",new Color(255,102,255)), 
	DAMAGED ("DM",new Color(153,76,0)),
	SEALED ("SD",new Color(0,153,153)),
	OPENED ("OP",new Color(0,102,102)),
	ONLINE("ONL",new Color(255,0,127));

	
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
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
	public Color getColor() {
		return color;
	}
	
	
	public ImageIcon getIcon()
	{
			var icon = new ImageIcon() {
				private static final long serialVersionUID = 1L;

				public void paintIcon(Component c, Graphics g, int x, int y) {
			        Graphics2D g2 = (Graphics2D) g;
			        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, MTGConstants.TABLE_ROW_HEIGHT, MTGConstants.TABLE_ROW_HEIGHT);
			        
			        g2.setColor(getColor());
			        g2.fill(circle);
			    }    
			};
			return icon;
	}
	
	
	
}

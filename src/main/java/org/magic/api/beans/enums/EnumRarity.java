package org.magic.api.beans.enums;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Comparator;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.interfaces.extra.MTGEnumeration;
import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.MTGConstants;

import com.google.gson.annotations.SerializedName;

public enum EnumRarity implements Comparator<EnumRarity>, MTGEnumeration, MTGIconable{

	@SerializedName(alternate = "common", value = "COMMON") 			COMMON (Color.BLACK,1),
	@SerializedName(alternate = "uncommon", value = "UNCOMMON") 	UNCOMMON (new Color(223, 223, 223),2),
	@SerializedName(alternate = "rare", value = "RARE") 							RARE (new Color(238, 230, 0 ),3),
	@SerializedName(alternate = "mythic", value = "MYTHIC") 					MYTHIC (new Color(240, 84, 16),4),
	@SerializedName(alternate = {"Special","special"}, value = "SPECIAL") 	SPECIAL (new Color(130, 113, 245),5),
	@SerializedName(alternate = {"Bonus","bonus"}, value = "BONUS") 		BONUS (new Color(238, 130, 238),6);

	private Color color;
	private int position;

	private EnumRarity(Color c,int position) {

		color=c;
		this.position=position;
	}

	public Color getColor()
	{
		return color;
	}

	public static EnumRarity rarityByName(String s)
	{
		try {
			return EnumRarity.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			logger.warn("Rarity {} is not found",s);
			return null;
		}
	}
	
	@Override	
	public Icon getIcon()
	{
			return new ImageIcon() {
				private static final long serialVersionUID = 1L;

				@Override
				public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
					var g2 = (Graphics2D) g;
			        var circle = new Ellipse2D.Double(0, 0, getIconWidth(), getIconHeight() );
			        g2.setColor(getColor());
			        g2.fill(circle);
			        
			        g2.setColor(Color.black);
			        g2.draw(circle);
			    }
				
				@Override
				public int getIconHeight() {
					return MTGConstants.TABLE_ROW_HEIGHT-2;
				}
				
				@Override
				public int getIconWidth() {
					return MTGConstants.TABLE_ROW_HEIGHT-2;
				}
				
				
				
			};
	}

	@Override
	public String toString() {
		return toPrettyString();
	}

	public int getPosition() {
		return position;
	}

	
	@Override
	public int compare(EnumRarity o1, EnumRarity o2) {
		return o1.getPosition()-o2.getPosition();
	}

}
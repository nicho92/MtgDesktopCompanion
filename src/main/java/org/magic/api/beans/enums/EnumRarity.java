package org.magic.api.beans.enums;

import java.awt.Color;
import java.util.Comparator;

import org.magic.api.interfaces.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumRarity implements Comparator<EnumRarity>, MTGEnumeration{

	@SerializedName(alternate = "common", value = "COMMON") 				COMMON (Color.BLACK,1),
	@SerializedName(alternate = "uncommon", value = "UNCOMMON") 			UNCOMMON (new Color(223, 223, 223),2),
	@SerializedName(alternate = "rare", value = "RARE") 					RARE (new Color(238, 230, 0 ),3),
	@SerializedName(alternate = "mythic", value = "MYTHIC") 				MYTHIC (new Color(240, 84, 16),4),
	@SerializedName(alternate = "timeshifted", value = "TIMESHIFTED") 		TIMESHIFTED (new Color(138, 63, 255),5),
	@SerializedName(alternate = {"Special","special"}, value = "SPECIAL") 	SPECIAL (Color.RED,6),
	@SerializedName(alternate = {"Bonus","bonus"}, value = "BONUS") 		BONUS (new Color(238, 130, 238),7);


	private Color color;
	private int position;


	private EnumRarity(Color c,int position) {

		color=c;
		this.position=position;
	}

	public Color toColor()
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
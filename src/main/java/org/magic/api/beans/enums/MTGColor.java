package org.magic.api.beans.enums;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum MTGColor implements Comparator<MTGColor>{

	@SerializedName(alternate = "White", value = "WHITE") 	WHITE ("W",Color.WHITE,1),
	@SerializedName(alternate = "Blue", value = "BLUE") 	BLUE ("U",new Color(33,129,226),2),
	@SerializedName(alternate = "Black", value = "BLACK") 	BLACK ("B",Color.BLACK,3),
	@SerializedName(alternate = "Red", value = "RED") 		RED ("R",new Color(214,10,10),4),
	@SerializedName(alternate = "Green", value = "GREEN") 	GREEN ("G",new Color(52,211,16),5),

	UNCOLOR ("C",Color.GRAY,0),
	GOLD ("",new Color(232,232,0),6);

	private String code;
	private Color color;
	private int position;


	public static MTGColor[] getColors()
	{
		return new MTGColor[] {WHITE,BLUE,BLACK,RED,GREEN,UNCOLOR};
	}


	private MTGColor(String s,Color c,int position) {
		code=s;
		color=c;
		this.position=position;
	}

	@Override
	public String toString() {
		return name();
	}

	public Color toColor()
	{
		return color;
	}

	public int getPosition() {
		return position;
	}

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}

	public String getCode()
	{
		return code;
	}

	public String toManaCode() {
		return "{"+getCode()+"}";
	}

	public static MTGColor colorByName(String s)
	{
		try {
			return MTGColor.valueOf(s.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			return null;
		}
	}

	public static MTGColor determine(List<MTGColor> colors)
	{
		if(colors==null || colors.isEmpty())
			return MTGColor.UNCOLOR;

		if(colors.size()>1)
			return MTGColor.GOLD;

		return colors.get(0);
	}


	public static MTGColor colorByCode(String s)
	{
		return List.of(MTGColor.values()).stream().filter(c->c.getCode().equalsIgnoreCase(s)).findAny().orElse(null);

	}


	public static List<MTGColor> parseByLabel(List<String> names)
	{
		return names.stream().map(MTGColor::colorByName).filter(Objects::nonNull).toList();
	}

	public static List<MTGColor> parseByCode(List<String> codes)
	{
		return codes.stream().map(MTGColor::colorByCode).filter(Objects::nonNull).toList();
	}


	@Override
	public int compare(MTGColor o1, MTGColor o2) {
		return o1.getPosition()-o2.getPosition();
	}

}
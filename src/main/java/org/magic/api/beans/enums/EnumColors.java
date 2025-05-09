package org.magic.api.beans.enums;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.swing.Icon;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.tools.UITools;

import com.google.gson.annotations.SerializedName;

public enum EnumColors implements Comparator<EnumColors>, MTGIconable{

	@SerializedName(alternate = "White", value = "WHITE") 	WHITE ("W",Color.WHITE,1),
	@SerializedName(alternate = "Blue", value = "BLUE") 	BLUE ("U",new Color(33,129,226),2),
	@SerializedName(alternate = "Black", value = "BLACK") 	BLACK ("B",Color.BLACK,3),
	@SerializedName(alternate = "Red", value = "RED") 		RED ("R",new Color(214,10,10),4),
	@SerializedName(alternate = "Green", value = "GREEN") 	GREEN ("G",new Color(52,211,16),5),

	UNCOLOR ("C",Color.GRAY,0),
	GOLD ("",new Color(232,232,0),6),
	SNOW ("S",new Color(232,232,0),6);
	
	
	private String code;
	private Color color;
	private int position;


	public static EnumColors[] getColors()
	{
		return new EnumColors[] {WHITE,BLUE,BLACK,RED,GREEN,UNCOLOR};
	}



	@Override
	public Icon getIcon() {
		return UITools.generateRoundedIcon(color);
	}


	@Override
	public String getName() {
		return toPrettyString();
	}
	
	
	private EnumColors(String s,Color c,int position) {
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

	public static EnumColors colorByName(String s)
	{
		try {
			return EnumColors.valueOf(s.toUpperCase());
		}
		catch(IllegalArgumentException _)
		{
			return null;
		}
	}

	public static EnumColors determine(List<EnumColors> colors)
	{
		if(colors==null || colors.isEmpty())
			return EnumColors.UNCOLOR;

		if(colors.size()>1)
			return EnumColors.GOLD;

		return colors.get(0);
	}


	public static EnumColors colorByCode(String s)
	{
		return List.of(EnumColors.values()).stream().filter(c->c.getCode().equalsIgnoreCase(s.trim())).findAny().orElse(null);
	}
	
	public static List<EnumColors> parseByManaCost(String c)
	{
		if(c==null || c.isEmpty())
			return new ArrayList<>();
		
		return Pattern.compile(EnumCardsPatterns.MANA_PATTERN.getPattern())
			        .matcher(c)
			        .results()
			        .map(mr->mr.group(1)).distinct()
			        .map(EnumColors::colorByCode)
			        .filter(Objects::nonNull)
			        .toList();
	}

	

	public static List<EnumColors> parseByLabel(List<String> names)
	{
		return names.stream().map(EnumColors::colorByName).filter(Objects::nonNull).toList();
	}

	public static List<EnumColors> parseByCode(List<String> codes)
	{
		return codes.stream().map(EnumColors::colorByCode).filter(Objects::nonNull).toList();
	}


	@Override
	public int compare(EnumColors o1, EnumColors o2) {
		return o1.getPosition()-o2.getPosition();
	}


}

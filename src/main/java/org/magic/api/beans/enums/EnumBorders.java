package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum EnumBorders{

	@SerializedName(alternate = "black", value = "BLACK") 				BLACK,
	@SerializedName(alternate = "borderless", value = "BORDERLESS") 	BORDERLESS,
	@SerializedName(alternate = "gold", value = "GOLD")       			GOLD,
	@SerializedName(alternate = "silver", value = "SILVER") 			SILVER,
	@SerializedName(alternate = "white", value = "WHITE") 				WHITE;

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}


	public static EnumBorders parseByLabel(String s)
	{
		try {
			return EnumBorders.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			return null;
		}
	}


}
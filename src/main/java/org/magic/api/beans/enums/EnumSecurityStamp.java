package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum EnumSecurityStamp{

	@SerializedName(alternate = "oval", value = "OVAL") 				OVAL,
	@SerializedName(alternate = "triangle", value = "TRIANGLE") 	TRIANGLE,
	@SerializedName(alternate = "arena", value = "ARENA")       			ARENA,
	@SerializedName(alternate = "acorn", value = "ACORN") 			ACORN,
	@SerializedName(alternate = "circle", value = "CIRCLE") 				CIRCLE,
	@SerializedName(alternate = "heart", value = "HEART") 				HEART;

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}


	public static EnumSecurityStamp parseByLabel(String s)
	{
		try {
			return EnumSecurityStamp.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			return null;
		}
	}


}
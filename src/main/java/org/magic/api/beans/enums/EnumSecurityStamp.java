package org.magic.api.beans.enums;

import org.magic.api.interfaces.extra.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumSecurityStamp implements MTGEnumeration{

	@SerializedName(alternate = "oval", value = "OVAL") 				OVAL,
	@SerializedName(alternate = "triangle", value = "TRIANGLE") 	TRIANGLE,
	@SerializedName(alternate = "arena", value = "ARENA")       	ARENA,
	@SerializedName(alternate = "acorn", value = "ACORN") 			ACORN,
	@SerializedName(alternate = "circle", value = "CIRCLE") 			CIRCLE,
	@SerializedName(alternate = "heart", value = "HEART") 			HEART,
	@SerializedName(alternate = "none", value = "NONE") 			NONE;

	public static EnumSecurityStamp parseByLabel(String s)
	{
		try {
			
			if(s==null)
				return EnumSecurityStamp.NONE;
			
			return EnumSecurityStamp.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			logger.warn("SecurityStamp {} is not found", s);
			return null;
		}
	}


}
package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.magic.api.interfaces.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumFinishes implements MTGEnumeration{

	@SerializedName(alternate = "etched", value = "ETCHED") 	 ETCHED,
	@SerializedName(alternate = "foil", value = "FOIL") 				 FOIL,
	@SerializedName(alternate = "glossy", value = "GLOSSY")     GLOSSY,
	@SerializedName(alternate = "nonfoil", value = "NONFOIL")  NONFOIL,
	@SerializedName(alternate = "signed", value = "SIGNED") 	 SIGNED;

	public static List<EnumFinishes> parseByLabel(List<String> names)
	{
		return names.stream().map(EnumFinishes::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static EnumFinishes parseByLabel(String s)
	{
		try {
			return EnumFinishes.valueOf(s.trim().toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			logger.warn("Finishe {} is not found",s);
			return null;
		}
	}


}

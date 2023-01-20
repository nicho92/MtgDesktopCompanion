package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum EnumFinishes{

	@SerializedName(alternate = "etched", value = "ETCHED") 	ETCHED,
	@SerializedName(alternate = "foil", value = "FOIL") 		FOIL,
	@SerializedName(alternate = "glossy", value = "GLOSSY")     GLOSSY,
	@SerializedName(alternate = "nonfoil", value = "NONFOIL") 	NONFOIL,
	@SerializedName(alternate = "signed", value = "SIGNED") 	SIGNED;

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}


	public static List<EnumFinishes> parseByLabel(List<String> names)
	{
		return names.stream().map(EnumFinishes::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static EnumFinishes parseByLabel(String s)
	{
		try {
			return EnumFinishes.valueOf(s.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			return null;
		}
	}


}
package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum MTGFinishes{

	@SerializedName(alternate = "etched", value = "ETCHED") 	ETCHED,
	@SerializedName(alternate = "foil", value = "FOIL") 		FOIL,
	@SerializedName(alternate = "glossy", value = "GLOSSY")     GLOSSY,
	@SerializedName(alternate = "nonfoil", value = "NONFOIL") 	NONFOIL,
	@SerializedName(alternate = "signed", value = "SIGNED") 	SIGNED;

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}


	public static List<MTGFinishes> parseByLabel(List<String> names)
	{
		return names.stream().map(MTGFinishes::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static MTGFinishes parseByLabel(String s)
	{
		try {
			return MTGFinishes.valueOf(s.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			return null;
		}
	}


}
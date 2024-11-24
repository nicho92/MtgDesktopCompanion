package org.magic.api.beans.enums;

import org.magic.api.interfaces.extra.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumLayout implements MTGEnumeration{

	
	@SerializedName(alternate = "adventure", value = "ADVENTURE")					ADVENTURE,
	@SerializedName(alternate = "aftermath", value = "AFTERMATH")						AFTERMATH,
	@SerializedName(alternate = "art_series", value = "ART_SERIES")						ART_SERIES,
	@SerializedName(alternate = "augment", value = "AUGMENT")							AUGMENT,
	@SerializedName(alternate = "case", value = "CASE")										CASE,
	@SerializedName(alternate = "class", value = "CLASS")										CLASS,
	@SerializedName(alternate = "companion", value = "COMPANION")					COMPANION,
	@SerializedName(alternate = "double_faced_token", value = "DOUBLE_FACED_TOKEN")		DOUBLE_FACED_TOKEN,
	@SerializedName(alternate = "double_sided", value = "DOUBLE_SIDED")			DOUBLE_SIDED,
	@SerializedName(alternate = "emblem", value = "EMBLEM")								EMBLEM,
	@SerializedName(alternate = "flip", value = "FLIP")       								    FLIP,
	@SerializedName(alternate = "host", value = "HOST")									    HOST,
	@SerializedName(alternate = "leveler", value = "LEVELER")								LEVELER,
	@SerializedName(alternate = "meld", value = "MELD") 									MELD,
	@SerializedName(alternate = "modal_dfc", value = "MODAL_DFC")					MODAL_DFC,
	@SerializedName(alternate = "mutate", value = "MUTATE")								MUTATE,
	@SerializedName(alternate = "normal", value = "NORMAL") 							NORMAL,
	@SerializedName(alternate = "planar", value = "PLANAR")								PLANAR,
	@SerializedName(alternate = "prototype", value = "PROTOTYPE")						PROTOTYPE,
	@SerializedName(alternate = "reversible_card", value = "REVERSIBLE_CARD")		REVERSIBLE_CARD,
	@SerializedName(alternate = "saga", value = "SAGA")										SAGA,
	@SerializedName(alternate = "scheme", value = "SCHEME")								SCHEME,
	@SerializedName(alternate = "split", value = "SPLIT") 										SPLIT,
	@SerializedName(alternate = "token", value = "TOKEN")									TOKEN,
	@SerializedName(alternate = "transform", value = "TRANSFORM") 					TRANSFORM,
	@SerializedName(alternate = "vanguard", value = "VANGUARD")						VANGUARD;

	
	public static EnumLayout parseByLabel(String s)
	{
		try {
			return EnumLayout.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			logger.warn("Layout {} is not found",s);
			return null;
		}
	}


}

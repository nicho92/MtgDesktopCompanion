package org.magic.api.beans.enums;

import org.magic.api.interfaces.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumLayout implements MTGEnumeration{

	
	@SerializedName(alternate = "normal", value = "NORMAL") 							NORMAL,
	@SerializedName(alternate = "split", value = "SPLIT") 								SPLIT,
	@SerializedName(alternate = "flip", value = "FLIP")       							FLIP,
	@SerializedName(alternate = "transform", value = "TRANSFORM") 						TRANSFORM,
	@SerializedName(alternate = "meld", value = "MELD") 								MELD,
	@SerializedName(alternate = "leveler", value = "LEVELER")							LEVELER,
	@SerializedName(alternate = "saga", value = "SAGA")									SAGA,
	@SerializedName(alternate = "planar", value = "PLANAR")								PLANAR,
	@SerializedName(alternate = "scheme", value = "SCHEME")								SCHEME,
	@SerializedName(alternate = "vanguard", value = "VANGUARD")							VANGUARD,
	@SerializedName(alternate = "token", value = "TOKEN")								TOKEN,
	@SerializedName(alternate = "double_faced_token", value = "DOUBLE_FACED_TOKEN")		DOUBLE_FACED_TOKEN,
	@SerializedName(alternate = "reversible_card", value = "REVERSIBLE_CARD")			REVERSIBLE_CARD,
	@SerializedName(alternate = "emblem", value = "EMBLEM")								EMBLEM,
	@SerializedName(alternate = "augment", value = "AUGMENT")							AUGMENT,
	@SerializedName(alternate = "aftermath", value = "AFTERMATH")						AFTERMATH,
	@SerializedName(alternate = "host", value = "HOST")									HOST,
	@SerializedName(alternate = "art_series", value = "ART_SERIES")						ART_SERIES,
	@SerializedName(alternate = "double_sided", value = "DOUBLE_SIDED")					DOUBLE_SIDED,
	@SerializedName(alternate = "adventure", value = "ADVENTURE")						ADVENTURE,
	@SerializedName(alternate = "companion", value = "COMPANION")						COMPANION,
	@SerializedName(alternate = "modal_dfc", value = "MODAL_DFC")						MODAL_DFC,
	@SerializedName(alternate = "class", value = "CLASS")								CLASS,
	@SerializedName(alternate = "prototype", value = "PROTOTYPE")						PROTOTYPE,
	@SerializedName(alternate = "mutate", value = "MUTATE")						MUTATE,
	@SerializedName(alternate = "case", value = "CASE")						CASE;

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

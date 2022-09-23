package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum MTGLayout{

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
	@SerializedName(alternate = "emblem", value = "EMBLEM")								EMBLEM,
	@SerializedName(alternate = "augment", value = "AUGMENT")							AUGMENT,
	@SerializedName(alternate = "aftermath", value = "AFTERMATH")						AFTERMATH,
	@SerializedName(alternate = "host", value = "HOST")									HOST,
	@SerializedName(alternate = "art_series", value = "ART_SERIES")						ART_SERIES,
	@SerializedName(alternate = "double_sided", value = "DOUBLE_SIDED")					DOUBLE_SIDED,
	@SerializedName(alternate = "adventure", value = "ADVENTURE")						ADVENTURE,
	@SerializedName(alternate = "companion", value = "COMPANION")						COMPANION,
	@SerializedName(alternate = "modal_dfc", value = "MODAL_DFC")						MODAL_DFC;



	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}

	public static List<MTGLayout> parseByLabel(List<String> names)
	{
		return names.stream().map(MTGLayout::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static MTGLayout parseByLabel(String s)
	{
		try {
			return MTGLayout.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			return null;
		}
	}


}
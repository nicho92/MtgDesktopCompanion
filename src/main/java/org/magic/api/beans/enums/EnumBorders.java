package org.magic.api.beans.enums;

import com.google.gson.annotations.SerializedName;
import org.magic.api.interfaces.extra.MTGEnumeration;

public enum EnumBorders implements MTGEnumeration {

	@SerializedName(alternate = "black", value = "BLACK")
	BLACK, @SerializedName(alternate = "borderless", value = "BORDERLESS")
	BORDERLESS, @SerializedName(alternate = "gold", value = "GOLD")
	GOLD, @SerializedName(alternate = "silver", value = "SILVER")
	SILVER, @SerializedName(alternate = "yellow", value = "YELLOW")
	YELLOW, @SerializedName(alternate = "white", value = "WHITE")
	WHITE;

	public static EnumBorders parseByLabel(String s) {
		try {
			return EnumBorders.valueOf(s.toUpperCase());
		} catch (Exception _) {
			logger.warn("Border {} is not found", s);
			return null;
		}
	}

}
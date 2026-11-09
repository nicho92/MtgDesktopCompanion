package org.magic.api.beans.enums;

import org.magic.api.interfaces.extra.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumFrameVersion implements MTGEnumeration{
    @SerializedName(value = "1993") FRAME_1993(1993),
    @SerializedName(value = "1997") FRAME_1997(1997),
    @SerializedName(value = "2003") FRAME_2003(2003),
    @SerializedName(value = "2015") FRAME_2015(2015),
    @SerializedName(value = "future") FRAME_FUTURE(9999);

    private int year;

    EnumFrameVersion(int i) {
	this.year=i;
    }
    
    public int getYear() {
	return year;
    }

    public static EnumFrameVersion parseByYear(String y) {
	try {
		return EnumFrameVersion.valueOf("FRAME_"+y.trim().toUpperCase());
	} catch (Exception _) {
		logger.warn("FrameVersion {} is not found", y);
		return null;
	}
    }

    
}

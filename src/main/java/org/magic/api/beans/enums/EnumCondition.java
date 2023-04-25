package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

public enum EnumCondition {

	MINT, NEAR_MINT, EXCELLENT, GOOD, LIGHTLY_PLAYED, PLAYED, POOR,PROXY, OVERSIZED,ONLINE,SEALED, OPENED, DAMAGED;


	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}

package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

public enum MTGCardVariation {

	SHOWCASE,
	EXTENDEDART,
	FULLART,
	BORDERLESS,
	TIMESHIFTED,
	JAPANESEALT;


	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}

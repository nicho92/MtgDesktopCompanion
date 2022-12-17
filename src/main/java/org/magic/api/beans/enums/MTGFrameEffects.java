package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public enum MTGFrameEffects{

	LEGENDARY,
	MIRACLE,
	NYXTOUCHED,
	NYXBORN,
	DRAFT,
	DEVOID,
	TOMBSTONE,
	COLORSHIFTED,
	INVERTED,
	SUNMOONDFC,
	COMPASSLANDDFC,
	ORIGINPWDFC,
	MOONELDRAZIDFC,
	MOONREVERSEMOONDFC,
	SHOWCASE,
	EXTENDEDART,
	LESSON,
	TEXTLESS,
	SNOW,
	COMPANION,
	WAXINGANDWANINGMOONDFC;

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}

	public static List<MTGFrameEffects> parseByLabel(List<String> names)
	{
		return names.stream().map(MTGFrameEffects::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static MTGFrameEffects parseByLabel(String s)
	{
		try {
			return MTGFrameEffects.valueOf(s.toUpperCase());
		}
		catch(Exception e)
		{
			return null;
		}
	}


}
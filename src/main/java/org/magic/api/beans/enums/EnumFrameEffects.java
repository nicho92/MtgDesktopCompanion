package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.interfaces.MTGEnumeration;

public enum EnumFrameEffects implements MTGEnumeration{

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
	FANDFC,
	EXTENDEDART,
	LESSON,
	TEXTLESS,
	SNOW,
	COMPANION,
	WAXINGANDWANINGMOONDFC,
	BORDERLESS,
	ETCHED,
	CONVERTDFC,
	SHATTEREDGLASS,
	FULLART;

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}

	public static List<EnumFrameEffects> parseByLabel(List<String> names)
	{
		return names.stream().map(EnumFrameEffects::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static EnumFrameEffects parseByLabel(String s)
	{
		try {
			return EnumFrameEffects.valueOf(s.trim().toUpperCase());
		}
		catch(Exception e)
		{
			logger.warn("FrameEffect {} is not found",s);
			return null;
		}
	}


}

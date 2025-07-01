package org.magic.api.beans.enums;

import java.util.List;
import java.util.Objects;

import org.magic.api.interfaces.extra.MTGEnumeration;

public enum EnumFrameEffects implements MTGEnumeration{
	NONE,
	BORDERLESS,
	COLORSHIFTED,
	COMPANION,
	COMPASSLANDDFC,
	CONVERTDFC,
	DEVOID,
	DRAFT,
	ENCHANTMENT,
	ETCHED,
	EXTENDEDART,
	FANDFC,
	FULLART,
	INVERTED,
	LEGENDARY,
	LESSON,
	MIRACLE,
	MOONELDRAZIDFC,
	MOONREVERSEMOONDFC,
	NYXBORN,
	NYXTOUCHED,
	ORIGINPWDFC,
	PROMO,
	SHATTEREDGLASS,
	SHOWCASE,
	SNOW,
	SPREE,
	STAMPED,
	SUNMOONDFC,
	TEXTLESS,
	TOMBSTONE,
	UPSIDEDOWNDFC,
	WAXINGANDWANINGMOONDFC;

	public static List<EnumFrameEffects> parseByLabel(List<String> names)
	{
		return names.stream().map(EnumFrameEffects::parseByLabel).filter(Objects::nonNull).toList();
	}

	public static EnumFrameEffects parseByLabel(String s)
	{
		try {
			return EnumFrameEffects.valueOf(s.trim().toUpperCase());
		}
		catch(Exception _)
		{
			logger.warn("FrameEffect {} is not found",s);
			return null;
		}
	}


}

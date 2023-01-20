package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.api.interfaces.MTGTokensProvider;

public abstract class AbstractTokensProvider extends AbstractMTGPlugin implements MTGTokensProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.TOKEN;
	}

	@Override
	public boolean isTokenizer(MagicCard mc) {
		return EnumCardsPatterns.hasPattern(mc.getText(), EnumCardsPatterns.CREATE_TOKEN);
	}

	@Override
	public boolean isEmblemizer(MagicCard mc) {
		return EnumCardsPatterns.hasPattern(mc.getText(), EnumCardsPatterns.CREATE_EMBLEM);
	}

}

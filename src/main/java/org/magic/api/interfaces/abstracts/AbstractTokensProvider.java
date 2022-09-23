package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.CardsPatterns;
import org.magic.api.interfaces.MTGTokensProvider;

public abstract class AbstractTokensProvider extends AbstractMTGPlugin implements MTGTokensProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.TOKEN;
	}

	@Override
	public boolean isTokenizer(MagicCard mc) {
		return CardsPatterns.hasPattern(mc.getText(), CardsPatterns.CREATE_TOKEN);
	}

	@Override
	public boolean isEmblemizer(MagicCard mc) {
		return CardsPatterns.hasPattern(mc.getText(), CardsPatterns.CREATE_EMBLEM);
	}

}

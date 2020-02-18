package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.tools.CardsPatterns;

public abstract class AbstractTokensProvider extends AbstractMTGPlugin implements MTGTokensProvider {

	
	@Override
	protected String getConfigDirectoryName() {
		return "tokens";
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.TOKENS;
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

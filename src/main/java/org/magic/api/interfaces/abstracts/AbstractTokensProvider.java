package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.CardsPatterns;

public abstract class AbstractTokensProvider extends AbstractMTGPlugin implements MTGTokensProvider {

	public AbstractTokensProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "tokens");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
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

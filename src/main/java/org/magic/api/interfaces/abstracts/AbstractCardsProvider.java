package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	public AbstractCardsProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "cardsProviders");
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}
	
	
}

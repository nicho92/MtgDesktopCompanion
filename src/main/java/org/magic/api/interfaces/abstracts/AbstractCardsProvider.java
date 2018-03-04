package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	public AbstractCardsProvider() {
		confdir = new File(MTGConstants.CONF_DIR, "cardsProviders");
		props=new Properties();

		if(!confdir.exists())
			confdir.mkdir();
		load();
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}
	
	
}

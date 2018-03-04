package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractTokensProvider extends AbstractMTGPlugin implements MTGTokensProvider {

	
	public AbstractTokensProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "tokens");
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.TOKENS;
	}
}

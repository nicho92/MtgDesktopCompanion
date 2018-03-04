package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.services.MTGConstants;

public abstract class AbstractMagicNewsProvider extends AbstractMTGPlugin implements MTGNewsProvider {

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.NEWS;
	}
	
	
	
	public AbstractMagicNewsProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "news");
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}

}

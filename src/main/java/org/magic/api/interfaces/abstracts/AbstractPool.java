package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGPool;
import org.magic.services.MTGConstants;

public abstract class AbstractPool extends AbstractMTGPlugin implements MTGPool {


	@Override
	public PLUGINS getType() {
		return PLUGINS.POOL;
	}
	
	
	public AbstractPool() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "pools");
		if (!confdir.exists())
			confdir.mkdir();
		load();
	
		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
		}
	
	
	
	
}

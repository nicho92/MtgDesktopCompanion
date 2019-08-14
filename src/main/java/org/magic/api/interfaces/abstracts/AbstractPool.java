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
	
	
	@Override
	public void initDefault() {
		setProperty("POOL_INIT_SIZE", "3");
		setProperty("POOL_MIN_IDLE", "3");
		setProperty("POOL_MAX_IDLE", "10");
		setProperty("POOL_MAX_SIZE", "10");
		setProperty("POOL_PREPARED_STATEMENT", "true");
	}
	
	
}

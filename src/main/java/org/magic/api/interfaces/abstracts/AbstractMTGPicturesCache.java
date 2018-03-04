package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGPicturesCache extends AbstractMTGPlugin implements MTGPicturesCache {

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}
	
	public AbstractMTGPicturesCache() {
		
		confdir = new File(MTGConstants.CONF_DIR, "caches");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
}

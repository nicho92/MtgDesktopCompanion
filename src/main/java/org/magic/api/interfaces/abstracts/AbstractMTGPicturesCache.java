package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGConstants;

public abstract class AbstractMTGPicturesCache extends AbstractMTGPlugin implements MTGPicturesCache {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}

	public AbstractMTGPicturesCache() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "caches");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();

		}
	}

}

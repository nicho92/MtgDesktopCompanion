package org.magic.api.interfaces.abstracts;

import java.io.File;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGConstants;
import org.magic.tools.IDGenerator;

public abstract class AbstractCacheProvider extends AbstractMTGPlugin implements MTGPicturesCache {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}

	


	protected String generateIdIndex(MagicCard mc, MagicEdition ed) {
		return IDGenerator.generate(mc, ed);
	}
	
	public AbstractCacheProvider() {
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

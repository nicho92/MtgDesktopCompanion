package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.services.tools.IDGenerator;

public abstract class AbstractCacheProvider extends AbstractMTGPlugin implements MTGPictureCache {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}


	protected String generateIdIndex(MagicCard mc) {
		return IDGenerator.generate(mc);
	}

}

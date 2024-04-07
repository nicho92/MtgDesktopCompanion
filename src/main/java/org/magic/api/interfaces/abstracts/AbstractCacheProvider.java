package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGPictureCache;

public abstract class AbstractCacheProvider extends AbstractMTGPlugin implements MTGPictureCache {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}


	protected String generateIdIndex(MTGCard mc) {
		if(mc==null)
			return "";
		return mc.getScryfallId();
	}

}

package org.magic.api.interfaces.abstracts;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCache;
import org.magic.tools.IDGenerator;

public abstract class AbstractCacheProvider extends AbstractMTGPlugin implements MTGCache {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}


	protected String generateIdIndex(MagicCard mc) {
		return IDGenerator.generate(mc, mc.getCurrentSet());
	}

}

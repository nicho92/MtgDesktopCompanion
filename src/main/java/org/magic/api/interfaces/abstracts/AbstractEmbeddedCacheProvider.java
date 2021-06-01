package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGCache;

public abstract class AbstractEmbeddedCacheProvider<U, V> extends AbstractMTGPlugin implements MTGCache<U, V> {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}


}

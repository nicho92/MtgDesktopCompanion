package org.magic.api.interfaces.abstracts.extra;

import java.util.Map;

import org.magic.api.interfaces.MTGCache;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;

public abstract class AbstractEmbeddedCacheProvider<U, V> extends AbstractMTGPlugin implements MTGCache<U, V> {

	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}

	public abstract Map<U, V> entries();
		


}

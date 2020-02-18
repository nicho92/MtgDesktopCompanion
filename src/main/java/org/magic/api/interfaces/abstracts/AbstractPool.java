package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGPool;

public abstract class AbstractPool extends AbstractMTGPlugin implements MTGPool {


	@Override
	public PLUGINS getType() {
		return PLUGINS.POOL;
	}
	
	
}

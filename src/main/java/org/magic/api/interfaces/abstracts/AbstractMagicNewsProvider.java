package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGNewsProvider;

public abstract class AbstractMagicNewsProvider extends AbstractMTGPlugin implements MTGNewsProvider {

	@Override
	public PLUGINS getType() {
		return PLUGINS.NEWS;
	}

}

package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGShopper;

public abstract class AbstractMagicShopper extends AbstractMTGPlugin implements MTGShopper {

	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
}

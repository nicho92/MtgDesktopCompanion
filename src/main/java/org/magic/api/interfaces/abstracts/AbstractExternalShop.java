package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGExternalShop;

public abstract class AbstractExternalShop extends AbstractMTGPlugin implements MTGExternalShop {

	@Override
	public PLUGINS getType() {
		return PLUGINS.EXTERNAL_SHOP;
	}



}

package org.magic.api.interfaces.abstracts;

import org.magic.api.interfaces.MTGNetworkClient;

public abstract class AbstractNetworkProvider extends AbstractMTGPlugin implements MTGNetworkClient {


	@Override
	public PLUGINS getType() {
		return PLUGINS.NETWORK;
	}


}

package org.magic.servers.impl;

import org.magic.api.interfaces.abstracts.AbstractWebServer;
import org.magic.services.MTGConstants;

public class ShoppingServer extends AbstractWebServer {

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Shopping Server";
	}

	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBSHOP_LOCATION;
	}

}

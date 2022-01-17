package org.magic.servers.impl;

import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.services.MTGConstants;

public class WebManagerServer extends AbstractWebServer {

	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBUI_LOCATION;
	}



	@Override
	public String description() {
		return "Web server front end";
	}

	@Override
	public String getName() {
		return "Web UI Server";
	}

	

}

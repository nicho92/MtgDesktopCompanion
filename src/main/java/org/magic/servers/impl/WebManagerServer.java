package org.magic.servers.impl;

import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;

public class WebManagerServer extends AbstractWebServer {

	@Override
	protected String getWebLocation() {
		return "web/web-ui";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
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

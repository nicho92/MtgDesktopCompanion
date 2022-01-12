package org.magic.servers.impl;

import org.magic.api.interfaces.abstracts.AbstractWebServer;
import org.magic.services.MTGConstants;

public class AdminWebDashboardServer extends AbstractWebServer {

	@Override
	protected String getWebLocation() {
		return MTGConstants.ADMINUI_LOCATION;
	}



	@Override
	public String description() {
		return "Web server administration dashboard";
	}

	@Override
	public String getName() {
		return "Web Dashboard Monitor";
	}

}

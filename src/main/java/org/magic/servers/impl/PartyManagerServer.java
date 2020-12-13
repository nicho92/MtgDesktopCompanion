package org.magic.servers.impl;

import javax.swing.Icon;

import org.magic.api.interfaces.abstracts.AbstractWebServer;
import org.magic.services.MTGConstants;

public class PartyManagerServer extends AbstractWebServer {

	@Override
	protected String getWebLocation() {
		return MTGConstants.EVENTUI_LOCATION;
	}


	@Override
	public String description() {
		return "Web server for tournament";
	}

	@Override
	public String getName() {
		return "Party Web Server";
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_GAME;
	}
	

}

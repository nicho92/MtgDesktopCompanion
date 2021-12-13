package org.magic.servers.impl;

import javax.swing.Icon;

import org.magic.api.interfaces.abstracts.AbstractWebServer;
import org.magic.services.MTGConstants;

public class TradesServer extends AbstractWebServer {
	@Override
	public String description() {
		return "Announces web page";
	}

	@Override
	public String getName() {
		return "WebTraders";
	}

	
	
	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBTRADES_LOCATION;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_ANNOUNCES;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
}

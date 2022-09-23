package org.magic.servers.impl;

import javax.swing.Icon;

import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.services.MTGConstants;

public class PricesWebServer extends AbstractWebServer {
	@Override
	public String description() {
		return "Get Price informations";
	}

	@Override
	public String getName() {
		return "Price Web Server";
	}



	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBPRICES_LOCATION;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DOLLARS;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

}

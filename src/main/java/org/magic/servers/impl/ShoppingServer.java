package org.magic.servers.impl;

import javax.swing.Icon;

import org.magic.api.interfaces.abstracts.AbstractWebServer;
import org.magic.services.MTGConstants;

public class ShoppingServer extends AbstractWebServer {

	@Override
	public String description() {
		return "Transaction web page";
	}

	@Override
	public String getName() {
		return "WebShop";
	}

	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBSHOP_LOCATION;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
}

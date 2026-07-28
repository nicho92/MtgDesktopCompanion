package org.magic.servers.impl;

import javax.swing.Icon;

import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.services.MTGConstants;

public class CollectionsWebServer extends AbstractWebServer {

	@Override
	public String description() {
		return "Generate web site to publish your cards to all the world";
	}

	@Override
	public String getName() {
		return "Collections web server";
	}

	@Override
	protected String getWebLocation() {
		return MTGConstants.WEBCOLLECTION_LOCATION;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_COLLECTION;
	}

}

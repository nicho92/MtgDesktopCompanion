package org.magic.servers.impl;

import java.util.Map;

import org.magic.api.interfaces.abstracts.extra.AbstractWebServer;
import org.magic.services.MTGConstants;

public class WebManagerServer extends AbstractWebServer {

	@Override
	protected String getWebLocation() {
		return "web/"+getString("TEMPLATE");
	}



	@Override
	public String description() {
		return "Web server front end";
	}

	@Override
	public String getName() {
		return "Web UI Server";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			m.put("TEMPLATE", "web-ui");
			
			return m;
	}

}

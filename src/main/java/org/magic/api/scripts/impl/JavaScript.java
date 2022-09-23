package org.magic.api.scripts.impl;

import java.util.Map;

import org.magic.api.interfaces.abstracts.extra.AbstractJSR223MTGScript;


public class JavaScript extends AbstractJSR223MTGScript {

	@Override
	public String getExtension() {
		return "js";
	}

	@Override
	public String getName() {
		return "Javascript";
	}

	@Override
	public String getEngineName() {
		return getString("ENGINE");
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("ENGINE", "rhino");
		return m;
	}

}

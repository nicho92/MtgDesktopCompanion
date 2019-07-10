package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.AbstractJSR223MTGScript;


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
		return "javascript";
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}
	
}

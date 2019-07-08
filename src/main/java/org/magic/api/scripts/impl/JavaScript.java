package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.AbstractMTGScript;


public class JavaScript extends AbstractMTGScript {

	@Override
	public String getExtension() {
		return "js";
	}

	@Override
	public String getName() {
		return "javascript";
	}
	
	@Override
	public String getVersion() {
		return engine.getFactory().getEngineVersion();
	}
}

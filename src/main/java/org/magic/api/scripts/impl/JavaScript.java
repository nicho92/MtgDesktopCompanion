package org.magic.api.scripts.impl;

import javax.script.ScriptEngineManager;

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
		if(engine==null)
			return new ScriptEngineManager().getEngineByName(getName()).getFactory().getEngineVersion();
		
		return engine.getFactory().getEngineVersion();
	}
}

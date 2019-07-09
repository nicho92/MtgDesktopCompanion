package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.AbstractJSR223MTGScript;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;

import groovy.lang.GroovySystem;

public class GroovyScript extends AbstractJSR223MTGScript {

	@Override
	public String getExtension()
	{
		return "groovy";
	}
	
	@Override
	public String getName()
	{
		return "Groovy";
	}
	
	@Override
	public String getVersion() {
		return GroovySystem.getVersion();
	}

	@Override
	public String getEngineName() {
		return "groovy";
	}
	
	
}

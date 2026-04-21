package org.magic.api.scripts.impl;

import groovy.lang.GroovySystem;
import org.magic.api.interfaces.abstracts.extra.AbstractJSR223MTGScript;

public class GroovyScript extends AbstractJSR223MTGScript {

	@Override
	public String getExtension() {
		return "groovy";
	}

	@Override
	public String getName() {
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

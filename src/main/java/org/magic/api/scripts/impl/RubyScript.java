package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.AbstractJSR223MTGScript;

public class RubyScript extends AbstractJSR223MTGScript {

	@Override
	public String getExtension() {
		return "rb";
	}

	@Override
	public String getName() {
		return "Ruby";
	}

	@Override
	public String getEngineName() {
		return "ruby";
	}

}

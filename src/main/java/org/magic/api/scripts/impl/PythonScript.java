package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.AbstractMTGScript;


public class PythonScript extends AbstractMTGScript {

	@Override
	public String getExtension() {
		return "py";
	}

	@Override
	public String getName() {
		return "Python";
	}

	@Override
	public String getVersion() {
		return engine.getFactory().getEngineVersion();
	}
}

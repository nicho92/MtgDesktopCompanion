package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.extra.AbstractJSR223MTGScript;
import org.python.Version;

public class PythonScript extends AbstractJSR223MTGScript {

	@Override
	public String getExtension() {
		return "py";
	}

	@Override
	public String getName() {
		return "Python";
	}

	@Override
	public String getEngineName() {
		return "python";
	}
	
	@Override
	public String getVersion() {
		return Version.PY_VERSION;
	}
}

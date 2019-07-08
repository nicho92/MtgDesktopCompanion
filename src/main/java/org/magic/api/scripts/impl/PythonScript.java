package org.magic.api.scripts.impl;

import org.magic.api.interfaces.abstracts.AbstractMTGScript;

import com.ziclix.python.sql.Jython22DataHandler;

public class PythonScript extends AbstractMTGScript {

	@Override
	public String getExtension() {
		return "py";
	}

	@Override
	public String getName() {
		return "Python";
	}

}

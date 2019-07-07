package org.magic.api.scripts;

import org.magic.api.interfaces.abstracts.AbstractMTGScript;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import jdk.jshell.JShell;

public class JShellScript extends AbstractMTGScript {

	@Override
	public String getExtension() {
		return "jsh";
	}

	@Override
	public Object run(String scriptName) throws ResourceException, ScriptException {
		
		JShell sh = JShell.create();
		
		return null;
	}

	@Override
	public String getName() {
		return "Java";
	}

}

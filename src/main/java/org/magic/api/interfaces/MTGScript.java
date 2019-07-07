package org.magic.api.interfaces;

import groovy.util.ResourceException;
import groovy.util.ScriptException;

public interface MTGScript extends MTGPlugin{

	public String getExtension();

	public Object run(String scriptName) throws ResourceException, ScriptException;

}

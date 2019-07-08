package org.magic.api.interfaces;

import java.io.File;

import groovy.util.ResourceException;
import groovy.util.ScriptException;

public interface MTGScript extends MTGPlugin{

	public String getExtension();

	public Object run(File script) throws ResourceException, ScriptException;
	
	public Object run(String scriptName) throws ResourceException, ScriptException;
	
	public Object runContent(String content) throws ResourceException, ScriptException;

}

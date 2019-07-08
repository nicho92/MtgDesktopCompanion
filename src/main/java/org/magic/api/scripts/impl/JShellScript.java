package org.magic.api.scripts.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;
import org.magic.services.MTGConstants;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

public class JShellScript extends AbstractMTGScript {

	@Override
	public String getExtension() {
		return "jsh";
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public Object runContent(String content) throws ResourceException, ScriptException {
		JShell sh = JShell.create();
			   sh.addToClasspath("org.magic.api.beans.*");
		List<SnippetEvent> ret =  sh.eval(content);
		logger.debug(ret);
		return ret;
	}

	@Override
	public String getName() {
		return "Java";
	}

	
}

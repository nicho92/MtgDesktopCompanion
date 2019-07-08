package org.magic.api.scripts.impl;

import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;
import javax.script.ScriptException;
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
	public Object runContent(String content) throws ScriptException {
		JShell sh = JShell.create();
			   sh.addToClasspath("org.magic.api.beans.*");
		List<SnippetEvent> ret =  sh.eval(content);
		logger.debug(ret);
		return ret;
	}
	
	@Override
	public String getVersion() {
		return SystemUtils.JAVA_VERSION;
	}

	@Override
	public String getName() {
		return "Java";
	}

	@Override
	public boolean isJsr223() {
		return false;
	}
	
}

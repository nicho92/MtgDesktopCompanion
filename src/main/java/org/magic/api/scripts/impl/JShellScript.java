package org.magic.api.scripts.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.script.ScriptException;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;

import jdk.jshell.JShell;

public class JShellScript extends AbstractMTGScript {

	
	private JShell sh;
	
	public JShellScript() {
		sh = JShell.create();
	}
	
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
		return sh.eval(content);
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
	public void addVariable(String k, Object o) {
				
	}
	
	
	@Override
	public void setOutput(Writer w) {
		sh=JShell.builder().out(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				w.write(b);
			}
		})).build();
	}
}

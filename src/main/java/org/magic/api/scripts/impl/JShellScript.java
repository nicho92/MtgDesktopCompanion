package org.magic.api.scripts.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;
import javax.script.ScriptException;
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

public class JShellScript extends AbstractMTGScript {

	
	private JShell sh;
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
		sh = JShell.create();
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
	public boolean isJsr223() {
		return false;
	}
	
	@Override
	public void addVariable(String k, Object o) {
		// TODO Auto-generated method stub
		
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

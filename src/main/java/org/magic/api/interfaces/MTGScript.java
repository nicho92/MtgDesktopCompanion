package org.magic.api.interfaces;

import java.io.File;
import java.io.Writer;

import javax.script.ScriptException;

public interface MTGScript extends MTGPlugin{

	public String getExtension();

	public Object run(File script) throws ScriptException;

	public Object run(String scriptName) throws ScriptException;

	public Object runContent(String content) throws ScriptException;

	public boolean isJsr223();

	public void setOutput(Writer w);

	public void addVariable(String k,Object o);

	public String getContentType();

	void init();

	public File getScriptDirectory();

}

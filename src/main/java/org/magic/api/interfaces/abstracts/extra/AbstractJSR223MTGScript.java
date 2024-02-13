package org.magic.api.interfaces.abstracts.extra;

import java.io.Writer;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.magic.api.interfaces.abstracts.AbstractMTGScript;

public abstract class AbstractJSR223MTGScript extends AbstractMTGScript  {

	public abstract String getEngineName();
	private Bindings binds;
	private ScriptEngine engine;

	public static AbstractJSR223MTGScript build(String name, String engineName,String extension ) {
		return new AbstractJSR223MTGScript() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getExtension() {
				return extension;
			}

			@Override
			public String getEngineName() {
				return engineName;
			}
		};
	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}


	@Override
	public void setOutput(Writer w) {
		if(engine==null)
			init();

			engine.getContext().setWriter(w);
	}


	@Override
	public Object runContent(String content) throws ScriptException {
		if(engine==null)
			init();
		
		return engine.eval(content,binds);
	}

	@Override
	public void addVariable(String k, Object o) {
		if(engine==null)
			init();

		binds.put(k, o);
	}

	@Override
	public void init() {

		if(engine==null)
		{
			engine = new ScriptEngineManager().getEngineByName(getEngineName());

			if(engine==null)
			{
				logger.error("{} is not found",getEngineName());
				return;
			}
			binds = engine.createBindings();
		}
		super.init();
	}

	@Override
	public String getVersion() {
		if(engine==null)
			init();

			try {
				return engine.getFactory().getEngineVersion();
			}
			catch(Exception e)
			{
				return "-1";
			}

	}

}

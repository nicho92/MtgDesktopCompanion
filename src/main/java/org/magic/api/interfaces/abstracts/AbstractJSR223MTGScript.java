package org.magic.api.interfaces.abstracts;

import java.io.Writer;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGControler;

public abstract class AbstractJSR223MTGScript extends AbstractMTGScript  {

	public abstract String getEngineName();
	protected Bindings binds;
	protected ScriptEngine engine;
	
	
	public AbstractJSR223MTGScript() {
		super();
	}
	
	@Override
	public String getVersion() {
		if(engine==null)
			init();
		
		return engine.getFactory().getEngineVersion();
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
	
	protected void init() {
		if(engine==null) {
			engine = new ScriptEngineManager().getEngineByName(getEngineName());
			
			if(engine==null)
			{
				logger.error(getEngineName() + " is not found");
				return;
			}
			binds = engine.createBindings();
			binds.put("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
			binds.put("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
			binds.put("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
		}
		else
		{
			logger.warn(getName() + " is not jsr223 compatible. engine property still null");
		}
	}
	

	public void test()
	{
		new ScriptEngineManager().getEngineFactories().forEach(f->logger.debug(f.getNames()));
	}

}

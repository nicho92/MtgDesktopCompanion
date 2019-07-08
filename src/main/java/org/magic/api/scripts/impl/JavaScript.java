package org.magic.api.scripts.impl;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;
import org.magic.services.MTGControler;

import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class JavaScript extends AbstractMTGScript {

	@Override
	public String getExtension() {
		return "js";
	}

	@Override
	public Object runContent(String content) throws ResourceException, ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		try {
		Bindings binds = engine.createBindings();
		binds.put("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
		binds.put("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
		binds.put("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
	
		return engine.eval(content,binds);
		}
		catch(Exception e)
		{
			throw new ScriptException(e);
		}
	}

	@Override
	public String getName() {
		return "javascript";
	}

}

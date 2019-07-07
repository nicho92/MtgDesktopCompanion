package org.beta;

import java.net.URL;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class GroovyLauncher {
	
	private Binding binding;
	private GroovyScriptEngine engine;
	
	
	public GroovyLauncher()
	{
		binding = new Binding();
		binding.setVariable("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
		binding.setVariable("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
		binding.setVariable("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
		engine = new GroovyScriptEngine(new URL[] {MTGConstants.SCRIPT_DIRECTORY});

	}
	
	public String getName()
	{
		return "Groovy";
	}
	
	public Object run(String scriptName) throws ResourceException, ScriptException
	{
		return engine.run(scriptName, binding);
	}
	
}

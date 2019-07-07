package org.magic.api.scripts;

import java.net.URL;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class GroovyScript extends AbstractMTGScript {
	
	private Binding binding;
	private GroovyScriptEngine engine;
	
	
	public GroovyScript()
	{
		binding = new Binding();
		engine = new GroovyScriptEngine(new URL[] {MTGConstants.SCRIPT_DIRECTORY});
		
	}
	
	@Override
	public String getExtension()
	{
		return "groovy";
	}
	
	@Override
	public String getName()
	{
		return "Groovy";
	}
	
	@Override
	public Object run(String scriptName) throws ResourceException, ScriptException
	{
		
		binding.setVariable("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
		binding.setVariable("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
		binding.setVariable("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
		
		return engine.run(scriptName+".groovy", binding);
	}
	
	
	
	
	public static void main(String[] args) throws ResourceException, ScriptException, SQLException {
		
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
		GroovyScript l = new GroovyScript();
		
		Object o = l.run("HelloWorld");
		
		System.out.println(o);
		
		
	}
	
}

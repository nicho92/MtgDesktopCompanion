package org.magic.api.scripts;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGScript;
import org.magic.services.MTGControler;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class GroovyScript extends AbstractMTGScript {
	private Binding binding;
	
	public GroovyScript()
	{
		super();
		binding = new Binding();
		binding.setVariable("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
		binding.setVariable("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
		binding.setVariable("picture", MTGControler.getInstance().getEnabled(MTGPictureProvider.class));
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
	public Object runContent(String content) throws ResourceException, ScriptException {
		return new GroovyShell(binding).evaluate(content);
	}
}

package org.beta;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class GroovyLauncher {

	public static void main(String[] args) throws IOException, URISyntaxException, ResourceException, ScriptException {
		Binding binding = new Binding();
				binding.setProperty("dao", MTGControler.getInstance().getEnabled(MTGDao.class));
				binding.setProperty("provider", MTGControler.getInstance().getEnabled(MTGCardsProvider.class));
		
				GroovyScriptEngine engine = new GroovyScriptEngine(new URL[] {MTGConstants.GROOVY_DIRECTORY});
				engine.run("HelloWorld.gy", binding);
				
		//GroovyShell shell = new GroovyShell(GroovyLauncher.class.getClassLoader(), binding);
		
		//File script = new File(new File(MTGConstants.GROOVY_DIRECTORY.toURI()), "HelloWorld.gy");
		//shell.evaluate(script);

	}

}

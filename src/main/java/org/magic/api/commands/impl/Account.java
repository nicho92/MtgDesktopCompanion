package org.magic.api.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.technical.AccountAuthenticator;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.TextResponse;
import org.magic.services.AccountsManager;
import org.magic.services.PluginRegistry;

public class Account extends AbstractCommand {

	@Override
	public void initOptions() {
		super.initOptions();
		
		opts.addOption("a", "add", true, " : add new accounts to ");
		opts.addOption("l", "list", false, " : list available plugins");
		opts.addOption("k", "key", true, " : set Keypass");
		opts.addOption("s", "save", false, " : save");
	}
	
	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException, InvocationTargetException, NoSuchMethodException {
		logger.debug("running {} with {}",this,Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		if (cl.hasOption("l")) {
			return new TextResponse(AccountsManager.inst().listAvailablePlugins().stream().map(MTGPlugin::getName).toList().toString());
		}
		
		if (cl.hasOption("a")) {
			
			var mtg = PluginRegistry.inst().getPlugin(cl.getOptionValue("a"));
			var acc = new AccountAuthenticator();	
			for( String s : cl.getArgList())
			{
				var k = s.split("=")[0];
				
				if(!mtg.getDefaultAttributes().keySet().contains(k))
					return new TextResponse(k + " isn't a valid attribute : " +  mtg.getDefaultAttributes().keySet());
				
				acc.addToken(k, s.split("=")[1]);
			}
		}
		
		if (cl.hasOption("s")) {
			AccountsManager.inst().saveConfig();
		}
		
		if (cl.hasOption("?")) {
			return usage();
		}
		
		return new TextResponse("done");
	}
}

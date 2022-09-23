package org.magic.api.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;
import org.magic.console.TextResponse;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class Plugin extends AbstractCommand {

	@Override
	public void initOptions() {
		super.initOptions();
		opts.addOption("l", "list", false, "list plugins");
		opts.addOption("n", "name", true, "get named plugin");
		opts.addOption("e", "enable", false, "enable");
		opts.addOption("d", "disable", false, "disable");
	}


	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException,IllegalAccessException, ParseException, IOException, InvocationTargetException, NoSuchMethodException {

		logger.debug("running {} with {}", this,Arrays.asList(args));

		CommandLine cl = parser.parse(opts, args);
		MTGPlugin plugin = null ;


		if (cl.hasOption("l")) {
			return new ArrayResponse(MTGPlugin.class, List.of("type","name","enabled","version"),json.convert(PluginRegistry.inst().listPlugins()));
		}

		if(cl.hasOption("n"))
		{
			String name = cl.getOptionValue("n");
			plugin = PluginRegistry.inst().getPlugin(name);
			MTGControler.getInstance().setProperty(plugin, plugin.isEnable());
		}

		if(cl.hasOption("e") || cl.hasOption("d"))
		{
			if(plugin == null)
				return new TextResponse("please use -n <name> to load plugin");


			plugin.enable(cl.hasOption("e"));
			MTGControler.getInstance().setProperty(plugin, plugin.isEnable());


			return new TextResponse(plugin + " " + plugin.isEnable() +" OK");


		}
		return usage();


	}
}

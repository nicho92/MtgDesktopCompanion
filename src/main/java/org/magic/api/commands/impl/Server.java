package org.magic.api.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;
import org.magic.console.TextResponse;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class Server extends AbstractCommand {

	@Override
	public void initOptions()  {
		super.initOptions();
		opts.addOption("n", "name", true, "server name");
		opts.addOption("s", "start", false, "start server");
		opts.addOption("c", "close", false, "stop server");
	}

	
	
	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException,IllegalAccessException, ParseException, IOException, InvocationTargetException, NoSuchMethodException {
		logger.debug("running "+ this +" with " + Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		MTGServer serv = null;
		
		if (cl.hasOption("n")) {
			serv = MTGControler.getInstance().getPlugin(cl.getOptionValue("n"), MTGServer.class);
		}
		else
		{
			return new TextResponse("You should use -n <servername> option");
		}
		
		
		if (cl.hasOption("s") && serv!=null) {
			serv.start();
			return new TextResponse(serv + " started");
		}
		
		
		if (cl.hasOption("c") && serv!=null) {
			serv.stop();
			return new TextResponse(serv + " stopped");
		}
		
		return new TextResponse("nothing append :(.. Please use -s or -c option to start or stop server "+serv);
		
		
	}

}

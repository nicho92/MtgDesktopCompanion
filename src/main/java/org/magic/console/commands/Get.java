package org.magic.console.commands;


import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicEdition;
import org.magic.console.AbstractCommand;
import org.magic.services.MTGControler;

public class Get extends AbstractCommand {

	public Get() {
		opts.addOption("n", "name", true, "get Card by name");
		opts.addOption("e", "equal", false, "strict search");
		opts.addOption("s", "set", true, "search in edition");
		opts.addOption("?", "help", false, "help for command");
	}
	
	@Override
	public Object run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException
	{	

		logger.debug("running "+ this +" with " + Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		String name=null;
		MagicEdition edition=null;
		boolean strict=cl.hasOption("e");
		
		
		if(cl.getOptions().length==0)
		{
			name = cl.getArgList().get(0);
			name = name.substring(name.indexOf(' '), name.length()).trim();
		}
		
		if (cl.hasOption("n")) {
			name = cl.getOptionValue("n");
		}
		
		if (cl.hasOption("s")) {
			edition = new MagicEdition();
			edition.setId(cl.getOptionValue("s"));
		}
		
		if(name!=null)
		{
			try {
				return MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(name,edition, strict).get(0);
			}catch(Exception e)
			{
				return null;
			}
		}
		
		return null;
	}

	

	@Override
	public String getCommandName() {
		return "get";
	}

}

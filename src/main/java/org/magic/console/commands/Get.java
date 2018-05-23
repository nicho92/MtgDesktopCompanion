package org.magic.console.commands;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class Get extends AbstractCommand {

	public Get() {
		
		opts.addOption("n", "name", true, "get Card by name");
		opts.addOption("s", "strict", false, "strict search");
		opts.addOption("e", "edition", true, "search in edition");
		opts.addOption("?", "help", false, "help for command");
	}
	
	@Override
	public Object run(String[] array) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException
	{	
		CommandLine cl = parser.parse(opts, array);
		
		String name=null;
		MagicEdition edition=null;
		boolean strict=cl.hasOption("s");
		
		if (cl.hasOption("n")) {
			name = cl.getOptionValue("n");
		}
		
		if (cl.hasOption("e")) {
			edition = new MagicEdition();
			edition.setId(cl.getOptionValue("e"));
		}
		
		if(name!=null)
		{
			return MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(name,edition, strict).get(0);
		}
		
		return null;
	}

	

	@Override
	public String getCommandName() {
		return "get";
	}

}

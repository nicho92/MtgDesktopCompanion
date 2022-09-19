package org.magic.api.commands.impl;


import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;

public class Get extends AbstractCommand {

	@Override
	public void initOptions() {
		super.initOptions();
		opts.addOption("n", "name", true, "get Card by name");
		opts.addOption("e", "equal", false, "strict search");
		opts.addOption("s", "set", true, "search in edition");
	}
	
	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException
	{	

		logger.debug("running {} with {}", this,Arrays.asList(args));
			
		CommandLine cl = parser.parse(opts, args);
		String name=null;
		MagicEdition edition=null;
		boolean strict=cl.hasOption("e");
		
		
		if (cl.hasOption("?")) {
			return usage();
		}
		
		if(cl.getOptions().length==0)
		{
			name = cl.getArgList().get(0);
			name = name.substring(name.indexOf(' '), name.length()).trim();
		}
		
		if (cl.hasOption("n")) {
			name = cl.getOptionValue("n");
		}
		
		if (cl.hasOption("s")) {
			edition = new MagicEdition(cl.getOptionValue("s"));
		}
		
		if(name!=null)
		{
			try {
				return new ArrayResponse(MagicCard.class, null,json.toJsonArray(getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name,edition, strict).get(0)));
			}catch(Exception e)
			{
				logger.error(e);
				return null;
			}
		}
		
		return null;
	}



}

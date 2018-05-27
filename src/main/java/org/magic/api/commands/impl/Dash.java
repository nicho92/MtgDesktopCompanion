package org.magic.api.commands.impl;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.CommandResponse;
import org.magic.services.MTGControler;

public class Dash extends AbstractCommand {

	@Override
	public void initOptions() {
		opts.addOption("f", "format", true, "get trending for a format");
		opts.addOption("c", "card", true, "get trending for a card");
		opts.addOption("s", "set", true, "get trending for a set");
		opts.addOption("t", "trending", true, "get trending for a set");
		opts.addOption("?", "help", false, "help for command");
		
	}


	@Override
	public CommandResponse<?> run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException
	{	

		logger.debug("running "+ this +" with " + Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		
		if (cl.hasOption("f")) {
			MTGFormat f = MTGFormat.valueOf(cl.getOptionValue("f").toUpperCase());
			return new CommandResponse<>(CardShake.class, null, json.toJsonElement(MTGControler.getInstance().getEnabledDashBoard().getShakerFor(f)));
		}
		
		if (cl.hasOption("s") && !cl.hasOption("c")) {
			MagicEdition ed = MTGControler.getInstance().getEnabledCardsProviders().getSetById(cl.getOptionValue("s"));
			return new CommandResponse<>(CardShake.class, null,json.toJsonElement(MTGControler.getInstance().getEnabledDashBoard().getShakeForEdition(ed)));
		}
		
		if (cl.hasOption("c")) {
			MagicEdition ed = null;
			if(cl.hasOption("s"))
				ed = MTGControler.getInstance().getEnabledCardsProviders().getSetById(cl.getOptionValue("s"));
			
			MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(cl.getOptionValue("c"), ed, false).get(0);
			return new CommandResponse<>(Map.class, null,json.toJsonElement(MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(mc, ed)));
		}
		
		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}



}

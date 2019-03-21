package org.magic.api.commands.impl;


import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;
import org.magic.services.MTGControler;

public class Dash extends AbstractCommand {

	@Override
	public void initOptions() {
		super.initOptions();
		opts.addOption("f", "format", true, "get trending for a format");
		opts.addOption("c", "card", true, "get trending for a card");
		opts.addOption("s", "set", true, "get trending for a set");
		opts.addOption("t", "trending", true, "get trending for a set");
	}


	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException
	{	

		logger.debug("running "+ this +" with " + Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		
		if (cl.hasOption("f")) {
			MagicFormat.FORMATS f = MagicFormat.FORMATS.valueOf(cl.getOptionValue("f").toUpperCase());
			return new ArrayResponse(CardShake.class, null, json.toJsonElement(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakerFor(f)));
		}
		
		if (cl.hasOption("s") && !cl.hasOption("c")) {
			MagicEdition ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(cl.getOptionValue("s"));
			return new ArrayResponse(CardShake.class, null,json.toJsonElement(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakesForEdition(ed)));
		}
		
		if (cl.hasOption("c")) {
			MagicEdition ed = null;
			if(cl.hasOption("s"))
				ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(cl.getOptionValue("s"));
			
			MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(cl.getOptionValue("c"), ed, false).get(0);
			return new ArrayResponse(CardPriceVariations.class, null,json.toJsonElement(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getPriceVariation(mc, ed)));
		}
		
		
		if (cl.hasOption("t")) {
			MagicEdition ed = null;
			ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(cl.getOptionValue("t"));
			return new ArrayResponse(CardShake.class, null,json.toJsonElement(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakesForEdition(ed)));
		}
		
		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}



}

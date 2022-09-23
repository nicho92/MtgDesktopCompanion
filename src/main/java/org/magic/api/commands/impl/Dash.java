package org.magic.api.commands.impl;


import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;

public class Dash extends AbstractCommand {

	@Override
	public void initOptions() {
		super.initOptions();
		opts.addOption("f", "format", true, "get trending for a format");
		opts.addOption("c", "card", true, "get trending for a card");
		opts.addOption("s", "set", true, "get trending for a set");
		opts.addOption("t", "trending", true, "get trending for a set");
		opts.addOption("foil", "foil", true, "get foil card values");
	}


	@Override
	public AbstractResponse run(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException
	{

		logger.debug("running {} with {}", this,Arrays.asList(args));


		CommandLine cl = parser.parse(opts, args);

		if (cl.hasOption("f")) {
			MagicFormat.FORMATS f = MagicFormat.FORMATS.valueOf(cl.getOptionValue("f").toUpperCase());
			return new ArrayResponse(CardShake.class, null, json.toJsonArray(getEnabledPlugin(MTGDashBoard.class).getShakerFor(f)));
		}

		if (cl.hasOption("s") && !cl.hasOption("c")) {
			MagicEdition ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(cl.getOptionValue("s"));
			return new ArrayResponse(CardShake.class, null,json.toJsonArray(getEnabledPlugin(MTGDashBoard.class).getShakesForEdition(ed),"shakes"));
		}

		if (cl.hasOption("c")) {
			MagicEdition ed = null;
			if(cl.hasOption("s"))
				ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(cl.getOptionValue("s"));

			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cl.getOptionValue("c"), ed, false).get(0);
			return new ArrayResponse(HistoryPrice.class, null,json.toJsonElement(getEnabledPlugin(MTGDashBoard.class).getPriceVariation(mc, cl.hasOption("foil"))).getAsJsonObject().get("variations").getAsJsonObject());
		}


		if (cl.hasOption("t")) {
			MagicEdition ed = null;
			ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(cl.getOptionValue("t"));
			return new ArrayResponse(CardShake.class, null,json.toJsonArray(getEnabledPlugin(MTGDashBoard.class).getShakesForEdition(ed),"shakes"));
		}

		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}



}

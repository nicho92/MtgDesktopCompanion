package org.magic.api.commands.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;

public class Search extends AbstractCommand {

	@Override
	public void initOptions()  {
		super.initOptions();
		opts.addOption("c", "cards", true, "search cards");
		opts.addOption("s", "set", false, "show all sets");
		opts.addOption("col", "cols", false, "show all collections");
	}

	@Override
	public AbstractResponse run(String[] args)throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

		logger.debug("running {} with {}", this,Arrays.asList(args));

		CommandLine cl = parser.parse(opts, args);

		if (cl.hasOption("c")) {
			String att = cl.getOptionValue("c").split("=")[0];
			String val = cl.getOptionValue("c").split("=")[1];
			return new ArrayResponse(MagicCard.class, null,json.toJsonArray(getEnabledPlugin(MTGCardsProvider.class).searchCardByCriteria(att, val, null,false)));
		}

		if (cl.hasOption("s")) {
			return new ArrayResponse(MagicEdition.class, null,json.toJsonArray(getEnabledPlugin(MTGCardsProvider.class).listEditions()));
		}

		if (cl.hasOption("col")) {
			List<MagicCollection> list;
			try {
				list = getEnabledPlugin(MTGDao.class).listCollections();
			} catch (SQLException e) {
				throw new IOException(e);
			}
			return new ArrayResponse(MagicCollection.class, null,json.toJsonArray(list));
		}

		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}


}

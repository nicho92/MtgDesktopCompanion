package org.magic.api.commands.impl;

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
import org.magic.services.MTGControler;

public class Search extends AbstractCommand {

	public void initOptions()  {
		opts.addOption("c", "cards", true, "search cards");
		opts.addOption("s", "set", false, "show all sets");
		opts.addOption("col", "cols", false, "show all collections");
		opts.addOption("?", "help", false, "help for command");
	}

	@Override
	public AbstractResponse<?> run(String[] args)throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
	
		logger.debug("running "+ this +" with " + Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		
		if (cl.hasOption("c")) {
			String att = cl.getOptionValue("c").split("=")[0];
			String val = cl.getOptionValue("c").split("=")[1];
			return new ArrayResponse<>(MagicCard.class, null,json.toJsonElement(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByCriteria(att, val, null,false)));
		}

		if (cl.hasOption("s")) {
			return new ArrayResponse<>(MagicEdition.class, null,json.toJsonElement(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions()));
		}

		if (cl.hasOption("col")) {
			List<MagicCollection> list;
			try {
				list = MTGControler.getInstance().getEnabled(MTGDao.class).listCollections();
			} catch (SQLException e) {
				throw new IOException(e);
			}
			return new ArrayResponse<>(MagicCollection.class, null,json.toJsonElement(list));
		}

		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}


}

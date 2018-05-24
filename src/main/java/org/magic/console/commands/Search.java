package org.magic.console.commands;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.MagicCollection;
import org.magic.console.AbstractCommand;
import org.magic.services.MTGControler;

public class Search extends AbstractCommand {

	public Search() {
		opts.addOption("c", "cards", true, "search cards");
		opts.addOption("s", "set", false, "show all sets");
		opts.addOption("col", "cols", false, "show all collections");
		opts.addOption("?", "help", false, "help for command");
	}

	@Override
	public Object run(String[] args)throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
	
		logger.debug("running "+ this +" with " + Arrays.asList(args));
		
		CommandLine cl = parser.parse(opts, args);
		
		if (cl.hasOption("c")) {
			String att = cl.getOptionValue("c").split("=")[0];
			String val = cl.getOptionValue("c").split("=")[1];
			return MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria(att, val, null,false);
		}

		if (cl.hasOption("s")) {
			return MTGControler.getInstance().getEnabledCardsProviders().loadEditions();
		}

		if (cl.hasOption("col")) {
			List<MagicCollection> list;
			try {
				list = MTGControler.getInstance().getEnabledDAO().getCollections();
			} catch (SQLException e) {
				throw new IOException(e);
			}
			return list;
		}

		if (cl.hasOption("?")) {
			return usage();
		}
		return "unknow";
	}

	@Override
	public String getCommandName() {
		return "search";
	}

}

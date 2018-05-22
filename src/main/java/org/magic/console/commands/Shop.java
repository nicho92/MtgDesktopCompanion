package org.magic.console.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MTGShopper;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class Shop extends AbstractCommand {

	public Shop() {
		opts.addOption("s", "search", true, "search item ");
		opts.addOption("l", "list", false, "list providers");
		opts.addOption("?", "help", false, "show usage");
	}

	@Override
	public Object run(String[] array)throws IOException, ParseException {
		CommandLine cl = parser.parse(opts, array);
		if (cl.hasOption("s")) {
			String att = cl.getOptionValue("s");
			List<ShopItem> list = new ArrayList<>();
			for (MTGShopper ms : MTGControler.getInstance().getShoppersProviders())
				list.addAll(ms.search(att));

			return (showList(list, Arrays.asList(MTGConsoleHandler.getAttShop())));
		}
		if (cl.hasOption("l")) {
			return (showList(MTGControler.getInstance().getShoppersProviders(), Arrays.asList("name", "enable")));
		}
		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "shop";
	}

}

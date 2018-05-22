package org.magic.console.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGFormat;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class Shake extends AbstractCommand {

	public Shake() {
		opts.addOption("f", "format", true, "show movement for format");
		opts.addOption("?", "help", false, "show usage");
	}

	@Override
	public Object run(String[] array)throws ParseException, IOException {
		CommandLine cl = parser.parse(opts, array);
		if (cl.hasOption("f")) {
			String att = cl.getOptionValue("f");
			List<CardShake> list = MTGControler.getInstance().getEnabledDashBoard().getShakerFor(MTGFormat.valueOf(att));
			return (showList(list, Arrays.asList(MTGConsoleHandler.getAttShake())));
		}

		if (cl.hasOption("?")) {
			return usage();
		}
		return null;
	}


	@Override
	public String getCommandName() {
		return "shake";
	}

}

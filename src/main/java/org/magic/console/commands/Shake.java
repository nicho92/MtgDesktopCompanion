package org.magic.console.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.mina.core.session.IoSession;
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
	public void run(String[] array, IoSession session, MTGConsoleHandler mtgConsoleHandler)
			throws ParseException, IOException {
		CommandLine cl = parser.parse(opts, array);
		this.session = session;

		if (cl.hasOption("f")) {
			String att = cl.getOptionValue("f");
			List<CardShake> list = MTGControler.getInstance().getEnabledDashBoard().getShakerFor(MTGFormat.valueOf(att));
			session.write(showList(list, Arrays.asList(MTGConsoleHandler.getAttShake())));
		}

		if (cl.hasOption("?")) {
			usage();
		}
	}

	@Override
	public void quit() {
		// do nothing

	}

	@Override
	public String getCommandName() {
		return "shake";
	}

}

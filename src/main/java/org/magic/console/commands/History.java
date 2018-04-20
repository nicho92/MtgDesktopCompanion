package org.magic.console.commands;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.mina.core.session.IoSession;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;

public class History extends AbstractCommand {

	public History() {
		opts.addOption("r", "reload", true, "relaunch history command with id");
		opts.addOption("?", "help", false, "show help");
	}

	@Override
	public void run(String[] array, IoSession session, MTGConsoleHandler mtgConsoleHandler)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, ParseException,
			InvocationTargetException, NoSuchMethodException {
		this.session = session;
		CommandLine cl = parser.parse(opts, array);
		if (cl.hasOption("r")) {
			String cmd = mtgConsoleHandler.getHistory().get(Integer.parseInt(cl.getOptionValue("r")));
			session.write("Reload " + cmd + "\r\n");
			mtgConsoleHandler.messageReceived(session, cmd);
		}
		if (cl.hasOption("?")) {
			usage();
		} else {

			session.write(showList(mtgConsoleHandler.getHistory()));
		}

	}

	private String showList(List<String> history) {
		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < history.size(); i++)
			temp.append(i + "\t" + history.get(i) + "\r\n");

		return temp.toString();
	}

	@Override
	public void quit() {
		// do nothing

	}

	@Override
	public String getCommandName() {
		return "history";
	}

}

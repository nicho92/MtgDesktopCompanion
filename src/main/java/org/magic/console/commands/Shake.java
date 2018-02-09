package org.magic.console.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.mina.core.session.IoSession;
import org.magic.api.beans.CardShake;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class Shake extends AbstractCommand {

	public Shake() {
		opts.addOption("f","format",true,"show movement for format");
		opts.addOption("?","help",false,"show usage");
	}
	
	@Override
	public void run(String[] array, IoSession session,MTGConsoleHandler mtgConsoleHandler) throws Exception {
		CommandLine cl = parser.parse(opts, array);
		this.session=session;
	
		if(cl.hasOption("f"))
		{
			String att = cl.getOptionValue("f");
			List<CardShake> list = MTGControler.getInstance().getEnabledDashBoard().getShakerFor(att);
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.getAttShake())));
		}
		
		if(cl.hasOption("?"))
		{
			usage();
		}
	}

	
	@Override
	public void quit() {
		

	}
	
	
	@Override
	public String getCommandName() {
		return "shake";
	}

}

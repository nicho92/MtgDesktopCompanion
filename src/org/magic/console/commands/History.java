package org.magic.console.commands;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.mina.core.session.IoSession;
import org.magic.console.Command;
import org.magic.console.MTGConsoleHandler;

public class History implements Command {

	
	public History() {
		opts.addOption("r","reload",true,"relaunch history command with id");
	}
	
	@Override
	public void run(String[] array, IoSession session,MTGConsoleHandler mtgConsoleHandler) throws Exception {
		CommandLine cl = parser.parse(opts, array);
		if(cl.hasOption("r"))
		{
			mtgConsoleHandler.messageReceived(session, mtgConsoleHandler.getHistory().get(Integer.parseInt(cl.getOptionValue("r"))));
		}
		else
		{
			
			session.write(showList(mtgConsoleHandler.getHistory()));
		}
		

	}

		
	private String showList(List<String> history) {
		StringBuffer temp = new StringBuffer();
		
		for(int i=0;i<history.size();i++)
			temp.append(i +"\t" + history.get(i)+"\r\n");
		
		return temp.toString();
	}

	@Override
	public void usage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

}

package org.magic.console.commands;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.mina.core.session.IoSession;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGLogger;

public class History extends AbstractCommand {

	
	private IoSession session;

	public History() {
		opts.addOption("r","reload",true,"relaunch history command with id");
		opts.addOption("?","help",false,"show help");
	}
	
	@Override
	public void run(String[] array, IoSession session,MTGConsoleHandler mtgConsoleHandler) throws Exception{
		this.session=session;
		CommandLine cl = parser.parse(opts, array);
		if(cl.hasOption("r"))
		{
			String cmd = mtgConsoleHandler.getHistory().get(Integer.parseInt(cl.getOptionValue("r")));
			session.write("Reload " + cmd +"\r\n");
			mtgConsoleHandler.messageReceived(session, cmd);
		}
		if(cl.hasOption("?"))
		{
			usage();
		}
		else
		{
			
			session.write(showList(mtgConsoleHandler.getHistory()));
		}
		

	}

		
	private String showList(List<String> history) {
		StringBuilder temp = new StringBuilder();
		
		for(int i=0;i<history.size();i++)
			temp.append(i +"\t" + history.get(i)+"\r\n");
		
		return temp.toString();
	}


	@Override
	public void usage() {
		HelpFormatter formatter = new HelpFormatter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintWriter ps = new PrintWriter(baos);
    	formatter.printHelp(ps,50, "history", null, opts, 0, 0, null);
    	ps.close();
     	try {
			session.write(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			MTGLogger.printStackTrace(e);
		}
	}

	@Override
	public void quit() {
		//do nothing

	}

}

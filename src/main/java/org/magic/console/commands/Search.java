package org.magic.console.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.mina.core.session.IoSession;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class Search extends AbstractCommand {

	public Search() {
		opts.addOption("c","cards",true,"search cards");
		opts.addOption("s","set",false,"show all sets");
		opts.addOption("col","cols",false,"show all collections");
		opts.addOption("?","help",false,"help for command");
	}
	
	@Override
	public void run(String[] args,IoSession session,MTGConsoleHandler mtgConsoleHandler) throws Exception {
		CommandLine cl = parser.parse(opts, args);
		this.session=session;
		if(cl.hasOption("c"))
		{
			String att = cl.getOptionValue("c").split("=")[0];
			String val = cl.getOptionValue("c").split("=")[1];
			List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(att, val, null,false);
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.getAttCards())));
		}
		
		if(cl.hasOption("s"))
		{
			List<MagicEdition> list = MTGControler.getInstance().getEnabledProviders().loadEditions();
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.getAttSet())));
		}
		
		if(cl.hasOption("col"))
		{
			List<MagicCollection> list = MTGControler.getInstance().getEnabledDAO().getCollections();
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.getAttCols())));
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
		return "search";
	}
	   

}

package org.magic.console.commands;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.mina.core.session.IoSession;
import org.asciitable.impl.ASCIITableImpl;
import org.asciitable.impl.CollectionASCIITableAware;
import org.asciitable.spec.IASCIITableAware;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.console.Command;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MagicFactory;

public class Search implements Command {

	
	private IoSession session;
	
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
			List<MagicCard> list = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria(att, val, null);
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.att_cards)));
		}
		
		if(cl.hasOption("s"))
		{
			List<MagicEdition> list = MagicFactory.getInstance().getEnabledProviders().searchSetByCriteria(null, null);
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.att_set)));
		}
		
		if(cl.hasOption("col"))
		{
			List<MagicCollection> list = MagicFactory.getInstance().getEnabledDAO().getCollections();
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.att_cols)));
		}
		
		if(cl.hasOption("?"))
		{
			usage();
		}
	}

	@Override
	public void usage() {
		HelpFormatter formatter = new HelpFormatter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintWriter ps = new PrintWriter(baos);
    	formatter.printHelp(ps,50, "search", null, opts, 0, 0, null);
    	ps.close();
     	try {
			session.write(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void quit() {

	}
	
	
	private <T> String showList(List<T> list,List<String> attributes) throws UnsupportedEncodingException
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintStream ps = new PrintStream(baos);
    	
    	IASCIITableAware asciiTableAware = new CollectionASCIITableAware<T>(list,attributes,attributes);
    	new ASCIITableImpl(ps).printTable(asciiTableAware);
    	
    	return new String(baos.toByteArray(),StandardCharsets.UTF_8);
    }
	   

}

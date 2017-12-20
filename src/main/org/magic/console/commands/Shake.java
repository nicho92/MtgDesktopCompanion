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
import org.magic.api.beans.CardShake;
import org.magic.console.Command;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;

public class Shake implements Command {

	
	private IoSession session;

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
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.att_shake)));
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
    	formatter.printHelp(ps,50, "shake", null, opts, 0, 0, null);
    	ps.close();
     	try {
			session.write(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
   
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

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

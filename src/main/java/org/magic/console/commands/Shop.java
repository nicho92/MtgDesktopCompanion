package org.magic.console.commands;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.mina.core.session.IoSession;
import org.asciitable.impl.ASCIITableImpl;
import org.asciitable.impl.CollectionASCIITableAware;
import org.asciitable.spec.IASCIITableAware;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MTGShopper;
import org.magic.console.AbstractCommand;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class Shop extends AbstractCommand {

	
	private IoSession session;

	public Shop() {
		opts.addOption("s","search",true,"search item ");
		opts.addOption("l","list",false,"list providers");
		opts.addOption("?","help",false,"show usage");
	}
	
	@Override
	public void run(String[] array, IoSession session,MTGConsoleHandler mtgConsoleHandler) throws Exception {
		CommandLine cl = parser.parse(opts, array);
		this.session=session;
	
		if(cl.hasOption("s"))
		{
			String att = cl.getOptionValue("s");
			List<ShopItem> list = new ArrayList<>();
			for(MTGShopper ms : MTGControler.getInstance().getShoppers())
				list.addAll(ms.search(att));
			
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.getAttShop())));
		}
		if(cl.hasOption("l"))
		{
			session.write(showList(MTGControler.getInstance().getShoppers(),Arrays.asList("shopName","enable")));
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
    	formatter.printHelp(ps,50, "shop", null, opts, 0, 0, null);
    	ps.close();
     	try {
			session.write(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			MTGLogger.printStackTrace(e);
		}
   
	}

	@Override
	public void quit() {
		// do nothing
	}
	

	private <T> String showList(List<T> list,List<String> attributes)
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintStream ps = new PrintStream(baos);
    	
    	IASCIITableAware asciiTableAware = new CollectionASCIITableAware<T>(list,attributes,attributes);
    	new ASCIITableImpl(ps).printTable(asciiTableAware);
    	
    	return new String(baos.toByteArray(),StandardCharsets.UTF_8);
    }

}

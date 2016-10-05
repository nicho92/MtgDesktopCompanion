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
import org.magic.api.interfaces.MagicShopper;
import org.magic.console.Command;
import org.magic.console.MTGConsoleHandler;
import org.magic.services.MagicFactory;

public class Shop implements Command {

	
	private IoSession session;

	public Shop() {
		opts.addOption("s","search",true,"search item ");
	}
	
	@Override
	public void run(String[] array, IoSession session,MTGConsoleHandler mtgConsoleHandler) throws Exception {
		CommandLine cl = parser.parse(opts, array);
		this.session=session;
	
		if(cl.hasOption("s"))
		{
			String att = cl.getOptionValue("s");
			List<ShopItem> list = new ArrayList<ShopItem>();
			for(MagicShopper ms : MagicFactory.getInstance().getShoppers())
				list.addAll(ms.search(att));
			
			session.write(showList(list,Arrays.asList(MTGConsoleHandler.att_shop)));
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

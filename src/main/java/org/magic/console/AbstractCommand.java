package org.magic.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.asciitable.impl.ASCIITableImpl;
import org.asciitable.impl.CollectionASCIITableAware;
import org.asciitable.spec.IASCIITableAware;
import org.magic.services.MTGLogger;

public abstract class AbstractCommand implements Command {

	protected CommandLineParser parser = new DefaultParser();
	protected Options opts = new Options();
	protected IoSession session;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	
	protected <T> String showList(List<T> list,List<String> attributes)
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintStream ps = new PrintStream(baos);
    	
    	IASCIITableAware asciiTableAware = new CollectionASCIITableAware<T>(list,attributes,attributes);
    	new ASCIITableImpl(ps).printTable(asciiTableAware);
    	
    	return new String(baos.toByteArray(),StandardCharsets.UTF_8);
    }
	
	@Override
	public void usage() {
		HelpFormatter formatter = new HelpFormatter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintWriter ps = new PrintWriter(baos);
    	formatter.printHelp(ps,50, getCommandName(), null, opts, 0, 0, null);
    	ps.close();
     	try {
			session.write(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
   
	}

}

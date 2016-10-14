package org.magic.console;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.mina.core.session.IoSession;

public interface Command {

	CommandLineParser parser = new DefaultParser();
	Options opts = new Options();
	
	
	public void run(String[] array, IoSession session, MTGConsoleHandler mtgConsoleHandler) throws Exception ;
	
	public void usage();
	
	public void quit();



}

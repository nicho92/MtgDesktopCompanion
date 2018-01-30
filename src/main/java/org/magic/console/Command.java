package org.magic.console;

import org.apache.mina.core.session.IoSession;

public interface Command {

	
	
	public void run(String[] array, IoSession session, MTGConsoleHandler mtgConsoleHandler) throws Exception ;
	
	public void usage();
	
	public void quit();



}

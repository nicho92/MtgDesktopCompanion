package org.magic.console;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.cli.ParseException;
import org.apache.mina.core.session.IoSession;

public interface Command {

	
	public void run(String[] array, IoSession session, MTGConsoleHandler mtgConsoleHandler) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException,IOException, InvocationTargetException, NoSuchMethodException;
	
	public void usage();
	
	public void quit();

    public String getCommandName();

}

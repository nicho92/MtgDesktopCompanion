package org.magic.api.interfaces;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.cli.ParseException;
import org.magic.console.AbstractResponse;
import org.magic.console.MTGConsoleHandler;

public interface MTGCommand extends MTGPlugin{

	public AbstractResponse run(String[] array) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException,InvocationTargetException, NoSuchMethodException;

	public AbstractResponse usage();

	public void quit();

	public String getCommandName();

	public void setHandler(MTGConsoleHandler handler);
}

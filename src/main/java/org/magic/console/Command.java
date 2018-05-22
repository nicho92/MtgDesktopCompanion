package org.magic.console;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.cli.ParseException;

public interface Command {

	public Object run(String[] array) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException,InvocationTargetException, NoSuchMethodException;

	public String usage();

	public void quit();

	public String getCommandName();

}

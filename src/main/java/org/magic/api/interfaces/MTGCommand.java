package org.magic.api.interfaces;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.cli.ParseException;
import org.magic.console.CommandResponse;

public interface MTGCommand extends MTGPlugin{

	public <T extends MTGCommand> CommandResponse<T> run(String[] array) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException,InvocationTargetException, NoSuchMethodException;

	public <T extends MTGCommand> CommandResponse usage();

	public void quit();

	public String getCommandName();


}

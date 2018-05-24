package org.magic.console;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.cli.ParseException;

import com.google.gson.JsonElement;

public interface Command {

	public JsonElement run(String[] array) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, IOException,InvocationTargetException, NoSuchMethodException;

	public JsonElement usage();

	public void quit();

	public String getCommandName();


}

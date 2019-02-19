package org.magic.api.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;

import org.apache.commons.cli.ParseException;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.ArrayResponse;
import org.magic.console.MTGConsoleHandler;
import org.magic.console.TextResponse;

public class History extends AbstractCommand {

	@Override
	public AbstractResponse run(String[] array) throws ClassNotFoundException, InstantiationException,IllegalAccessException, ParseException, IOException, InvocationTargetException, NoSuchMethodException {
		return new TextResponse(String.join(MTGConsoleHandler.EOL, handler.getHistory()));
		
	}

	@Override
	public void initOptions() {
		
	}

}

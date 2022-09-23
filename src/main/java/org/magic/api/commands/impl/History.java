package org.magic.api.commands.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.cli.ParseException;
import org.magic.api.interfaces.abstracts.AbstractCommand;
import org.magic.console.AbstractResponse;
import org.magic.console.MTGConsoleHandler;
import org.magic.console.TextResponse;

public class History extends AbstractCommand {

	@Override
	public AbstractResponse run(String[] array) throws ClassNotFoundException, InstantiationException,IllegalAccessException, ParseException, IOException, InvocationTargetException, NoSuchMethodException {

		var temp = new StringBuilder();

		handler.getHistory().forEach(c->temp.append(c.toString()).append(MTGConsoleHandler.EOL));

		return new TextResponse(temp.toString());

	}

	@Override
	public void initOptions() {
		//do nothing
	}

}

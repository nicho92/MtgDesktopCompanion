package org.magic.services.logging;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class MTGLogger {

	protected static final Level[] LEVELS = Level.values();

	private MTGLogger() {}

	public static Level[] getLevels()
	{
		return LEVELS;
	}

	public static Logger getLogger(Class<?> c) {
		return getLogger(c.getName());
	}

	public static Logger getLogger(String n) {
		return getContext().getLogger(n);
	}

	public static void changeLevel(String l) {
		changeLevel(Level.toLevel(l));
	}

	public static void changeLevel(Logger logger, String l) {
		
		var lev = logger.getLevel();
		
		try {
			lev=  Level.toLevel(l);
		}
		catch(Exception e)
		{
			logger.error("Error setting logger level to {}",l);
			
		}
		Configurator.setLevel(logger, lev);
		getContext().updateLoggers();
	}

	public static void changeLevel(Level l) {
		Configurator.setRootLevel(l);
	}
	
	private static LoggerContext getContext()
	{
		return (LoggerContext) LogManager.getContext(true);
	}
	

	public static Appender getAppender(String name) {
		return getContext().getConfiguration().getAppender(name);
	}

	public static List<LoggerConfig> getLoggers() {
		return new ArrayList<>(getContext().getConfiguration().getLoggers().values());
	}

	public static List<Appender> getAppenders() {
		return getContext().getRootLogger().getAppenders().entrySet().stream().map(Entry::getValue).toList();
	}

	public static MTGAppender getMTGAppender() {
		
		return (MTGAppender) getAppender("APPS");
	}

}

package org.magic.services.logging;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
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
		return LogManager.getLogger(n);
	}

	public static void changeLevel(String l) {
		changeLevel(Level.toLevel(l));
	}

	public static void changeLevel(Logger logger, String l) {
		Configurator.setLevel(logger, Level.toLevel(l));
	}

	public static void changeLevel(Level l) {
		Configurator.setRootLevel(l);
	}

	public static Appender getAppender(String name) {
		var	logContext = (LoggerContext) LogManager.getContext(false);
		return logContext.getConfiguration().getAppender(name);
	}

	public static List<LoggerConfig> getLoggers() {
		var	logContext = (LoggerContext) LogManager.getContext(false);
		return new ArrayList<>(logContext.getConfiguration().getLoggers().values());
	}

	public static List<Appender> getAppenders() {
		var	logContext = (LoggerContext) LogManager.getContext(false);
		return logContext.getRootLogger().getAppenders().entrySet().stream().map(Entry::getValue).toList();
	}

	public static MTGAppender getMTGAppender() {
		return (MTGAppender) getAppender("APPS");
	}

}

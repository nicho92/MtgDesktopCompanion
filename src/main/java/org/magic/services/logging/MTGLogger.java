package org.magic.services.logging;


import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.magic.services.logging.MTGAppender;

public class MTGLogger {

	protected static final Level[] LEVELS = Level.values();

	private MTGLogger() {
	}
	

	public static Level[] getLevels()
	{
		return LEVELS;
	}

	public static Logger getLogger(Class<?> c) {
		return LogManager.getLogger(c.getName());
	}
	
	public static Logger getLogger(String pckg) {
		return LogManager.getLogger(pckg);
	}

	public static void changeLevel(Level l) {
		Configurator.setRootLevel(l);
	}

	public static void changeLevel(String l) {
		changeLevel(Level.toLevel(l));
	}
	

	public static void changeLevel(Logger logger, String string) {
		Configurator.setLevel(logger, Level.toLevel(string));
		
	}

	public static Appender getAppender(String name) {
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		return logContext.getConfiguration().getAppender(name);
	}

	public static Map<String, LoggerConfig> getLoggers() {
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		return logContext.getConfiguration().getLoggers();
	}

	public static List<Appender> getAppenders() {
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		return logContext.getRootLogger().getAppenders().entrySet().stream().map(e->e.getValue()).toList();
	}

	public static MTGAppender getMTGAppender() {
		if ((MTGAppender) getAppender("APPS") == null) {
			return new MTGAppender("APPS",null);
		}
		return (MTGAppender) getAppender("APPS");
	}


}

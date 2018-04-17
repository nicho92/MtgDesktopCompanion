package org.magic.services;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MTGLogger {

	
	private MTGLogger() {
	}
	
	
	public static Logger getLogger(Class<?> c)
	{
		return LogManager.getLogger(c.getName());
	}
	
	public static void changeLevel(Level l)
	{
		LogManager.getRootLogger().setLevel(l);
	}
	
	public static void changeLevel(String l)
	{
		changeLevel(Level.toLevel(l));
	}
	
	public static Appender getAppender(String name)
	{
		return Logger.getRootLogger().getAppender(name);
	}
	
	public static List<Logger> getLoggers()
	{
		return Collections.list(LogManager.getCurrentLoggers());
	}
	
	public static List<Appender> getAppenders()
	{
		return Collections.list(LogManager.getRootLogger().getAllAppenders());
	}
	
	public static MTGAppender getMTGAppender()
	{
		if((MTGAppender)getAppender("APPS")==null)
		{
			System.err.println("appender APPS is not found");
			return new MTGAppender();
		}
		return (MTGAppender)getAppender("APPS");
	}

	
}

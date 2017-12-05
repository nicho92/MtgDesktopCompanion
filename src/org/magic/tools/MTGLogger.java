package org.magic.tools;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.services.MTGAppender;

public class MTGLogger {

	
	public static Logger getLogger(Class c)
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
	
	public static List<Appender> getAppenders()
	{
		return Collections.list(LogManager.getRootLogger().getAllAppenders());
	}
	
	public static MTGAppender getMTGAppender()
	{
		return (MTGAppender)getAppender("APPS");
	}
	
	
	public static void main(String[] args) {
		for(Appender l : MTGLogger.getAppenders())
		{
			System.out.println(l.getName() +" " + l.getClass());
		}
	}
	
}

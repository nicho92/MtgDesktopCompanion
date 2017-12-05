package org.magic.tools;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MTGLogger {

	
	public static Logger getLogger(Class c)
	{
		return LogManager.getLogger(c.getName());
	}
	
	public static void changeLevel(Level l)
	{
		LogManager.getRootLogger().setLevel(l);
	}
	
}

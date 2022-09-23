package org.beta;

import org.apache.commons.lang3.SystemUtils;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class WikiPagesBuilder
{
	
	
	
	public boolean isBoolean(String s)
	{
		return s.equals("true") || s.equals("false");
	}
	
	public boolean isNumber(String s)
	{
		try {
			Integer.parseInt(s);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	
	
	
	public static void main(String[] args) {
		
		MTGControler.getInstance();
		
		for(var plugin : PluginRegistry.inst().listPlugins())
		{
			
			for(var e : plugin.getDefaultAttributes().entrySet())
			{
				var value = e.getValue().replace(SystemUtils.USER_HOME, "$USER_HOME");
			}
		}
		System.exit(0);
		
	}
	
}
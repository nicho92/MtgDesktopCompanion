package org.magic.tools;

import java.util.List;

import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class MTG {
	
	private MTG() {
		// this class tool is for simple controler classes access
	}
	
	public static String lang(String key)
	{
		return MTGControler.getInstance().getLangService().get(key);
	}
	
	public static String capitalize(String key)
	{
		return MTGControler.getInstance().getLangService().getCapitalize(key);
	}

	public static <T extends MTGPlugin> T getEnabledPlugin(Class<T> t) 
	{
		return PluginRegistry.inst().getEnabledPlugins(t);
	}

	public static <T extends MTGPlugin> List<T> listEnabledPlugins(Class<T> t) 
	{
		return PluginRegistry.inst().listEnabledPlugins(t);
	}
	
	public static <T extends MTGPlugin> T getPlugin(String name,Class<T> type) {
		return PluginRegistry.inst().getPlugin(name,type);
	}
	
	public static <T extends MTGPlugin> List<T> listPlugins(Class<T> t)
	{
		return PluginRegistry.inst().listPlugins(t);
	}
	
	
	
}

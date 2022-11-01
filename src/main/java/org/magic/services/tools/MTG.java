package org.magic.services.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.logging.MTGLogger;

public class MTG {
	protected static Logger logger = MTGLogger.getLogger(MTG.class);

	private MTG() {
		// this class tool is for simple controler classes access
	}

	public static String lang(String key)
	{
		return MTGControler.getInstance().getLangService().get(key);
	}

	public static String capitalize(String key, Object... o)
	{
		return MTGControler.getInstance().getLangService().getCapitalize(key,o);
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
		return PluginRegistry.inst().listEnabledPlugins(t).stream().sorted(Comparator.comparing(MTGPlugin::getName)).toList();
	}

	public static <T extends MTGPlugin> List<T> listPlugins(String[] names, Class<T> t)
	{
		List<T> list = new ArrayList<>();
		for(String name : names)
		{
			if(!StringUtils.isEmpty(name))
				try {
					list.add(PluginRegistry.inst().getPlugin(name,t));
				}
				catch(Exception e)
				{
					logger.error("Can't add {} {} : {}" ,name,t,e);
				}
		}
		return list;
	}



	public static <T extends MTGPlugin> T getPlugin(String name,Class<T> type) {
		return PluginRegistry.inst().getPlugin(name,type);
	}

	public static <T extends MTGPlugin> List<T> listPlugins(Class<T> t)
	{
		return PluginRegistry.inst().listPlugins(t);
	}



}

package org.magic.services.providers;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.pricers.impl.CardTraderPricer;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class PluginsAliasesProvider {

	private JsonObject jsonData;
	private Logger logger = MTGLogger.getLogger(PluginsAliasesProvider.class);
	private static PluginsAliasesProvider inst;
	private boolean useLocalAliases = false; //true uses local source

	public static PluginsAliasesProvider inst()
	{
		if(inst==null)
			inst = new PluginsAliasesProvider();
		
		
		
		return inst;
	}

	private PluginsAliasesProvider() {

		if (useLocalAliases) {
			try {
				logger.error(MTGConstants.MTG_DESKTOP_ALIASES_FILE);
				jsonData = URLTools.toJson(MTGConstants.MTG_DESKTOP_ALIASES_FILE.openStream()).getAsJsonObject();
			}
			catch(Exception e) {
				logger.error("No Error getting file {} : {}",MTGConstants.MTG_DESKTOP_ALIASES_URL,e.getMessage());
			}
		}
		else {
			try {
				jsonData = URLTools.extractAsJson(MTGConstants.MTG_DESKTOP_ALIASES_URL).getAsJsonObject();
			}
			catch(Exception e)
			{
				logger.error("No Error getting file {} : {}",MTGConstants.MTG_DESKTOP_ALIASES_URL,e.getMessage());
			}
		}
	}

	public String getReversedSetIdFor(MTGPlugin plug, MTGEdition set)
	{
		return getReversedSetIdFor(plug, set.getId());
	}


	public String getReversedSetIdFor(MTGPlugin plug, String setId)
	{
		try{
			var ret= jsonData.get(plug.getName()).getAsJsonObject().get("idSet").getAsJsonObject().entrySet().stream().filter(e->e.getValue().getAsString().equals(setId)).findFirst().orElseThrow();
			return ret.getKey();
		}
		catch(Exception _)
		{
			return setId;
		}
	}

	public String getReversedSetNameFor(MTGPlugin plug, String setName)
	{
		try{
			var ret= jsonData.get(plug.getName()).getAsJsonObject().get("nameSet").getAsJsonObject().entrySet().stream().filter(e->e.getValue().getAsString().equals(setName)).findFirst().orElseThrow();
			return ret.getKey();
		}
		catch(Exception e)
		{
			logger.error(e);
			return setName;
		}
	}
	
	public static void main(String[] args) {
		PluginsAliasesProvider.inst().getReversedConditionFor(new CardTraderPricer(), "MODERATELY_PLAYED", null);
	}
	
	

	public EnumCondition getReversedConditionFor(MTGPlugin plug, String conditionName, EnumCondition defaultCondition)
	{
		if(conditionName==null || conditionName.isEmpty())
			return defaultCondition;
		
		try{
			var ret= jsonData.get(plug.getName()).getAsJsonObject().get("conditions").getAsJsonObject().entrySet().stream().filter(e->e.getValue().getAsString().equals(conditionName)).findFirst().orElseThrow();
			return EnumCondition.valueOf(ret.getKey());
		}
		catch(Exception e)
		{
			logger.warn("can't get condition value \"{}\" for {} : msg={}",conditionName,plug,e.getMessage());
			return defaultCondition;
		}
	}
	
	public String getRegexFor(MTGPlugin plug,String k)
	{
		try {
			return  jsonData.get(plug.getName()).getAsJsonObject().get("regex").getAsJsonObject().get(k).getAsString();
		}
		catch(Exception _)
		{
			logger.error("error getting regex for {} with key={}. Return default", plug.getName(),k);
			return jsonData.get(plug.getName()).getAsJsonObject().get("regex").getAsJsonObject().get("default").getAsString();
		}
	}

	public String getConditionFor(MTGPlugin plug, EnumCondition condition)
	{
		
		if(condition==null)
			return "";
		
		try{
			return jsonData.get(plug.getName()).getAsJsonObject().get("conditions").getAsJsonObject().get(condition.name()).getAsString();
		}
		catch(Exception _)
		{
			logger.error("Error getting condition {} for plug {}",condition,plug);
			return condition.name();
		}
	}




	public String getSetNameFor(MTGPlugin plug, MTGEdition ed)
	{
		return getSetNameFor(plug,ed.getSet());
	}


	public String getSetIdFor(MTGPlugin plug, MTGEdition ed)
	{
		return getSetIdFor(plug,ed.getId());
	}


	public String getSetIdFor(MTGPlugin plug, String ed)
	{
		try{
			return jsonData.get(plug.getName()).getAsJsonObject().get("idSet").getAsJsonObject().get(ed).getAsString();
		}
		catch(Exception _)
		{
			return ed;
		}
	}

	public String getSetNameFor(MTGPlugin plug, String ed)
	{
		try{
		return  jsonData.get(plug.getName()).getAsJsonObject().get("nameSet").getAsJsonObject().get(ed).getAsString();
		}
		catch(Exception _)
		{
			return ed;
		}
	}


}

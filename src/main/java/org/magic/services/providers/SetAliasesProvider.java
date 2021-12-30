package org.magic.services.providers;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class SetAliasesProvider {

	private JsonObject jsonData;
	private Logger logger = MTGLogger.getLogger(SetAliasesProvider.class);
	private static SetAliasesProvider inst;
	
	public static SetAliasesProvider inst()
	{
		if(inst==null)
			inst = new SetAliasesProvider();
		
		return inst;
	}
	
	
	
	private SetAliasesProvider() {
		try {
			jsonData = URLTools.extractAsJson(MTGConstants.MTG_DESKTOP_ALIASES_URL).getAsJsonObject();
		}
		catch(Exception e)
		{
			logger.error("No Error getting file "+  MTGConstants.MTG_DESKTOP_ALIASES_URL + " :"+e);
		}
	}
	
	
	public String getReversedSetIdFor(MTGPlugin plug, String setId)
	{
		try{
			var ret= jsonData.get(plug.getName()).getAsJsonObject().get("idSet").getAsJsonObject().entrySet().stream().filter(e->e.getValue().getAsString().equals(setId)).findFirst().orElseThrow();
			return ret.getKey();
		}
		catch(Exception e)
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
	
	public String getSetNameFor(MTGPlugin plug, MagicEdition ed)
	{
		try{
			return jsonData.get(plug.getName()).getAsJsonObject().get("nameSet").getAsJsonObject().get(ed.getSet()).getAsString();
		}
		catch(Exception e)
		{
			return ed.getId();
		}
	}
	
	
	public String getSetIdFor(MTGPlugin plug, MagicEdition ed)
	{
		try{
			return jsonData.get(plug.getName()).getAsJsonObject().get("idSet").getAsJsonObject().get(ed.getId()).getAsString();
		}
		catch(Exception e)
		{
			return ed.getId();
		}
	}
	
	
	
	
}

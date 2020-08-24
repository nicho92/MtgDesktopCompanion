package org.magic.tools;

import com.google.gson.JsonObject;

public class MTGArenaTools {

	
	public static JsonObject readCollection(String contentFile)
	{
		return readForToken("PlayerInventory.GetPlayerCardsV3", contentFile);
	}
	
	
	
	public static JsonObject readDecks(String contentFile)
	{
		return readForToken("Deck.GetDeckListsV3", contentFile);
		
	}
	
	
	private static JsonObject readForToken(String token,String contentFile)
	{
		String json =contentFile.substring(token.length()+contentFile.lastIndexOf(token));
		json = json.substring(0,json.indexOf(System.lineSeparator())).trim();
		return URLTools.toJson(json).getAsJsonObject();
	}
	
}

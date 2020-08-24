package org.magic.tools;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonObject;

public class MTGArenaTools {

	private File arenaFile;
	
	public MTGArenaTools(File arenaFile)
	{
		this.arenaFile = arenaFile;
	}
	
	public JsonObject readCollection() throws IOException
	{
		return readForToken("PlayerInventory.GetPlayerCardsV3");
	}

	
	
	public JsonObject readDecks() throws IOException
	{
		return readForToken("Deck.GetDeckListsV3");
		
	}
	
	
	public String getAccount()
	{
		
		String contentFile;
		try {
			contentFile = FileTools.readFile(arenaFile);
			String token = "Successfully logged in to account:";
			
			contentFile = contentFile.substring(contentFile.indexOf(token)+token.length());
			contentFile = contentFile.substring(0,contentFile.indexOf(System.lineSeparator()));
			
			return contentFile.trim();
			
		} catch (IOException e) {
			return "";
		}
	}
	
	private JsonObject readForToken(String token) throws IOException
	{
		String contentFile = FileTools.readFile(arenaFile);
		String json =contentFile.substring(token.length()+contentFile.lastIndexOf(token));
		json = json.substring(0,json.indexOf(System.lineSeparator())).trim();
		return URLTools.toJson(json).getAsJsonObject();
	}
	
}

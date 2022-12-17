package org.magic.services.tools;

import java.io.File;
import java.io.IOException;

import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class MTGArenaTools {


	private String contentFile;

	public MTGArenaTools(File arenaFile) throws IOException
	{
		contentFile = FileTools.readFile(arenaFile);
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
		var token = "Successfully logged in to account:";

		var content= contentFile.substring(contentFile.indexOf(token)+token.length());
			content = content.substring(0,contentFile.indexOf(System.lineSeparator()));

			return content.trim();

	}

	private JsonObject readForToken(String token) throws IOException
	{
		try {
			var json =contentFile.substring(token.length()+contentFile.lastIndexOf(token));
			json = json.substring(0,json.indexOf(System.lineSeparator())).trim();
			return URLTools.toJson(json).getAsJsonObject();
		} catch (Exception e) {
			throw new IOException("MTGArena is not installed or detailled log are not enabled ? please read the documentation");
		}
	}

}

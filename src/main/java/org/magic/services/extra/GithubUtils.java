package org.magic.services.extra;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GithubUtils {

	private JsonObject latest;
	private JsonArray releases;
	private static GithubUtils inst;
	
	
	
	public static GithubUtils inst() throws IOException
	{
		if(inst==null)
			inst = new GithubUtils();
		
		return inst;
	}
	
	private GithubUtils() throws IOException {
		releases = URLTools.extractJson(MTGConstants.MTG_DESKTOP_GITHUB_RELEASE_API).getAsJsonArray();
		latest = releases.get(0).getAsJsonObject();
	}
	
	
	public String getReleaseURL()
	{
		return latest.get("html_url").getAsString();
	}
	
	public String getAuthor()
	{
		return latest.get("author").getAsJsonObject().get("login").getAsString();
	}
	
	public String getVersion()
	{
		return latest.get("tag_name").getAsString();
	}
	
	public String getVersionName()
	{
		return latest.get("name").getAsString();
	}
	
	public BufferedImage getAvatar()
	{
		try {
			return URLTools.extractImage(latest.get("author").getAsJsonObject().get("avatar_url").getAsString());
		} catch (IOException e) {
			return null;
		}
	}
	
	public int downloadCount()
	{
		int count=0;
		for(JsonElement obj : releases)
		{
			if(obj.getAsJsonObject().get("assets").getAsJsonArray().size()>0)
				count += obj.getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("download_count").getAsInt();
		}
		return count;
	}
	
	public String downloadUrl()
	{
		return latest.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
	}
	
	
}

package org.magic.services;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.tools.URLTools;

import com.google.gson.JsonObject;

public class GithubUtils {

	private JsonObject obj;
	private static GithubUtils inst;
	
	
	
	public static GithubUtils inst() throws IOException
	{
		if(inst==null)
			inst = new GithubUtils();
		
		return inst;
	}
	
	
	
	private GithubUtils() throws IOException {
		obj = URLTools.extractJson(MTGConstants.MTG_DESKTOP_GITHUB_RELEASE_URL).getAsJsonObject();
	}
	
	
	public String getAuthor()
	{
		return obj.get("author").getAsJsonObject().get("login").getAsString();
	}
	
	public String getVersion()
	{
		return obj.get("tag_name").getAsString();
	}
	
	public String getVersionName()
	{
		return obj.get("name").getAsString();
	}
	
	public BufferedImage getAvatar()
	{
		try {
			return URLTools.extractImage(obj.get("author").getAsJsonObject().get("avatar_url").getAsString());
		} catch (IOException e) {
			return null;
		}
	}
	
	public int downloadCount()
	{
		return obj.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("download_count").getAsInt();
	}
	
	public String downloadUrl()
	{
		return obj.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
	}
	
	
}

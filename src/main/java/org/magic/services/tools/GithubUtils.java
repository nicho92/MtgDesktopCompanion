
package org.magic.services.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GithubUtils {

	private static final String ASSETS = "assets";
	private JsonObject selectedRelease;
	private JsonArray releases;
	private static GithubUtils inst;
	private boolean updatetoprerelease=false;


	public static GithubUtils inst() 
	{
		if(inst==null)
			inst = new GithubUtils();

		return inst;
	}

	public void setUpdateToPreRelease(boolean b)
	{
		updatetoprerelease=b;
		update();
	}


	private GithubUtils() {
		releases = URLTools.extractAsJson(MTGConstants.MTG_DESKTOP_GITHUB_RELEASE_API).getAsJsonArray();
		update();
	}

	private void update() {

		var selected=0;

		if(!updatetoprerelease)
			while(releases.get(selected).getAsJsonObject().get("prerelease").getAsBoolean())
				selected++;


		setSelectedRelease(selected);

	}

	public JsonArray getReleases() {
		return releases;
	}

	public JsonObject getSelectedRelease() {
		return selectedRelease;
	}

	public void setSelectedRelease(int index)
	{
		selectedRelease = releases.get(index).getAsJsonObject();
	}

	public String getReleaseURL()
	{
		return selectedRelease.get("html_url").getAsString();
	}

	public String getReleaseContent()
	{
		return selectedRelease.get("body").getAsString();
	}


	public String getAuthor()
	{
		return selectedRelease.get("author").getAsJsonObject().get("login").getAsString();
	}

	public String getVersion()
	{
		return selectedRelease.get("tag_name").getAsString();
	}

	public String getVersionName()
	{
		return selectedRelease.get("name").getAsString();
	}

	public BufferedImage getAvatar()
	{
		try {
			return URLTools.extractAsImage(selectedRelease.get("author").getAsJsonObject().get("avatar_url").getAsString());
		} catch (IOException _) {
			return null;
		}
	}

	public int downloadCount()
	{
		var count=0;
		for(JsonElement obj : releases)
		{
			if(obj.getAsJsonObject().get(ASSETS).getAsJsonArray().size()>0)
				count += obj.getAsJsonObject().get(ASSETS).getAsJsonArray().get(0).getAsJsonObject().get("download_count").getAsInt();
		}
		return count;
	}

	public String downloadUrl()
	{
		return selectedRelease.get(ASSETS).getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
	}


}

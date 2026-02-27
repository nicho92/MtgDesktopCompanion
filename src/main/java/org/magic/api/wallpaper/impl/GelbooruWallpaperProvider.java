package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GelbooruWallpaperProvider extends AbstractJsonWallpaperProvider{
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY","USER_ID");
	}

	@Override
	protected String getPaginationKey() {
		return "pid";
	}

	@Override
	protected int getResultsPerPage() {
		return 100;
	}
	

	@Override
	protected MTGWallpaper parse(JsonObject el) {
		try {
		var pic = new MTGWallpaper();
			pic.setAuthor(el.get("owner").getAsString());
			pic.setFormat(el.get("image").getAsString().substring(el.get("image").getAsString().length()-3));
			pic.setPublishDate(new Date(el.get("change").getAsLong()*1000));
			pic.setName(el.get("id").getAsString());
			pic.setProvider(getName());
			pic.setUrlThumb(URI.create(el.get("preview_url").getAsString()));
			pic.setUrl(URI.create(el.get("file_url").getAsString()));
			pic.addHeader(URLTools.REFERER, "https://gelbooru.com");
			for(var s : el.get("tags").getAsString().split(" "))
				pic.getTags().add(s);
			
			
			return pic;
		}
		catch(Exception _)
		{
			logger.error("error parsing json for {}",el);
			return null;
		}
		
	}
	
	@Override
	protected RequestBuilder createQuery(String search, int pidStart) {
		return RequestBuilder.build().newClient().url("https://gelbooru.com/index.php").get()
		.addContent("page", "dapi")
		.addContent("s","post")
		.addContent("q", "index")
		.addContent("limit", String.valueOf(getResultsPerPage()))		
		.addContent(getPaginationKey(), String.valueOf(pidStart))
		.addContent("json","1")
		.addContent("api_key", getAuthenticator().get("API_KEY"))
		.addContent("user_id", getAuthenticator().get("USER_ID"))
		.addContent("tags", search.toLowerCase().replace(" ", "_"));
	}
	
	@Override
	protected JsonArray extractArrayFromQuery(RequestBuilder req) {
		var je = req.toJson();
		
		if(je==null || je.isJsonNull())
			return new JsonArray(0);
		
		if(je.getAsJsonObject().get("post")==null)
			return new JsonArray(0);
		
		
		return je.getAsJsonObject().get("post").getAsJsonArray();
		
	}
	
	@Override
	public String getName() {
		return "Gelbooru";
	}

}

package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class GelbooruWallpaperProvider extends AbstractWallpaperProvider{
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY","USER_ID");
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",MTGProperty.newIntegerProperty("150", "Max results to return", 1, -1));
	}
	
	@Override
	public List<MTGWallpaper> search(String search) {
		
		var ret = new ArrayList<MTGWallpaper>();
		var page = 0;
		var total = 100; 
		
		while(ret.size()<total)
		{
			try {
			var baseUrl ="https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&pid="+(page++)+"&limit=100&api_key="+getAuthenticator().get("API_KEY")+"&user_id="+getAuthenticator().get("USER_ID")+"&tags="+search.replace(" ", "_");
			logger.debug("Get results from {}",baseUrl);
			var obj =  URLTools.extractAsJson(baseUrl).getAsJsonObject();
			total = obj.get("@attributes").getAsJsonObject().get("count").getAsInt();
			
			if(obj.get("post")==null)
				break;
			
			for(var je : obj.get("post").getAsJsonArray())
			{
				var w = parseJson(je.getAsJsonObject());
				if(w!=null)
					ret.add(w);
				
				if(ret.size()>=getInt("LIMIT"))
				{
					logger.info("{} return {} results", getName(), ret.size());
					return ret;
				}
				
			}
			}catch(Exception e)
			{
				logger.error("error in {}", getName(),e);
			}
		}
		logger.info("{} return {} results", getName(), ret.size());
		return ret;
	}

	private MTGWallpaper parseJson(JsonObject el) {
		try {
		var pic = new MTGWallpaper();
			pic.setAuthor(el.get("owner").getAsString());
			pic.setFormat(el.get("image").getAsString().substring(el.get("image").getAsString().length()-3));
			pic.setPublishDate(new Date(el.get("change").getAsLong()*1000));
			pic.setName(el.get("id").getAsString());
			pic.setProvider(getName());
			pic.setUrlThumb(URI.create(el.get("preview_url").getAsString()));
			pic.setUrl(URI.create(el.get("file_url").getAsString()));
			
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
	public String getName() {
		return "Gelbooru";
	}

}

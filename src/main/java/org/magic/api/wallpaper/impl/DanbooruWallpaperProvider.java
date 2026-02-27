package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class DanbooruWallpaperProvider extends AbstractWallpaperProvider{
	
	private String userAgent = "gallery-dl/1.30.5-dev";
	
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
		var page = 1;
		var c = URLTools.newClient();
		
		while(ret.size()<getInt("LIMIT"))
		{
			try {
			
			var result = RequestBuilder.build().get().setClient(c)
													.url("https://danbooru.donmai.us/posts.json")
													.addContent("page",String.valueOf(page))
													.addContent("limit","100")
													.addContent("api_key",getAuthenticator().get("API_KEY"))
													.addContent("login",getAuthenticator().get("USER_ID"))
													.addContent("tags",search.replace(" ", "_"))
													.addHeader(URLTools.USER_AGENT, userAgent)
													.toJson();
			var arr = result.getAsJsonArray();
						
			if(arr.isEmpty())
				break;
			
				for(var je : arr)
				{
					var w = parseJson(je.getAsJsonObject());
					
					if(w!=null)
						ret.add(w);
					
					if(ret.size()>=getInt("LIMIT"))
						break;
					
				}
				
				if(ret.size()>=getInt("LIMIT"))
					break;
				
				page++;
				
			}
			catch(Exception e)
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
			pic.setAuthor(el.get("tag_string_artist").getAsString());
			pic.setFormat(el.get("file_ext").getAsString());
			pic.setName(el.get("id").getAsString());
			pic.setProvider(getName());
			pic.setUrlThumb(URI.create(el.get("preview_file_url").getAsString()));
			pic.setUrl(URI.create(el.get("large_file_url").getAsString()));
			pic.setPublishDate(Date.from(OffsetDateTime.parse(el.get("updated_at").getAsString()).toInstant()));
			pic.addHeader(URLTools.USER_AGENT, userAgent);
			pic.setMature(true);
			
			for(var s : el.get("tag_string").getAsString().split(" "))
				pic.getTags().add(s);
		
			
			return pic;
		}
		catch(Exception e)
		{
			logger.error("error parsing json for {} : {}",el,e);
			return null;
		}
		
	}

	@Override
	public String getName() {
		return "Danbooru";
	}

}

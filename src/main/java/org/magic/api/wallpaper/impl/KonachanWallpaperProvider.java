package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.URLTools;

public class KonachanWallpaperProvider extends AbstractWallpaperProvider{

	@Override
	public List<MTGWallpaper> search(String search) {
		
		var page=1;
		
		var ret = new ArrayList<MTGWallpaper>();
		
		
		while(ret.size()<getInt("LIMIT"))
		{
			var baseUrl ="https://konachan.com/post.json?limit=100&page="+(page++)+"&tags="+search.replace(" ", "_");
			
			var arr = URLTools.extractAsJson(baseUrl).getAsJsonArray();
			
			if(arr.isEmpty())
			{
				logger.info("{} return nothing", getName());
				return ret;
			}
			
			for(var je : arr)
			{
				var el = je.getAsJsonObject();
				
				var pic = new MTGWallpaper();
					pic.setAuthor(el.get("author").getAsString());
					pic.setFormat(el.get("file_url").getAsString().substring(el.get("file_url").getAsString().length()-3));
					pic.setUrlThumb(URI.create(el.get("preview_url").getAsString()));
					pic.setUrl(URI.create(el.get("file_url").getAsString()));
					pic.setPublishDate(new Date(el.get("created_at").getAsLong()*1000));
					pic.setName(el.get("id").getAsString());
					pic.setProvider(getName());
					for(var s : el.get("tags").getAsString().split(" "))
						pic.getTags().add(s);
				
				ret.add(pic);
				
				if(ret.size()>=getInt("LIMIT"))
					break;
				
			}
		}
		
		logger.info("{} return {} results", getName(), ret.size());
		return ret;
	}

	@Override
	public String getName() {
		return "Konachan";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",MTGProperty.newIntegerProperty("150", "Max results to return", 1, -1));
	}
	
}

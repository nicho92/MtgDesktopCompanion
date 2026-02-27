package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class YandereWallpaperProvider extends AbstractWallpaperProvider{

	@Override
	public List<MTGWallpaper> search(String search) {
		var baseUrl ="https://yande.re/post.json?limit="+getInt("LIMIT")+"&tags="+search.replace(" ", "_");
		
		var ret = new ArrayList<MTGWallpaper>();
		
		
		for(var je : URLTools.extractAsJson(baseUrl).getAsJsonArray())
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
				pic.addHeader(URLTools.USER_AGENT,MTGConstants.MTG_APP_NAME); 
				for(var s : el.get("tags").getAsString().split(" "))
					pic.getTags().add(s);
				
				
			ret.add(pic);
		}
		return ret;
	}

	@Override
	public String getName() {
		return "YandeRe";
	}

	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",MTGProperty.newIntegerProperty("10", "Max results to return", 1, -1));
	}
}

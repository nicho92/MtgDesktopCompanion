package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.URLTools;

public class YandereWallpaperProvider extends AbstractWallpaperProvider{

	@Override
	public List<MTGWallpaper> search(String search) {
		var baseUrl ="https://yande.re/post.json?limit=10&tags="+search.replace(" ", "_");
		
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
			ret.add(pic);
		}
		return ret;
	}

	@Override
	public String getName() {
		return "YandeRe";
	}

}

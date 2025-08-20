package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.URLTools;

public class PixivWallpaperProvider extends AbstractWallpaperProvider {

	@Override
	public List<MTGWallpaper> search(String search) {
		
		var ret = new ArrayList<MTGWallpaper>();
		
		search = search.replace(" ", "%20");
		
		var url="https://www.pixiv.net/ajax/search/artworks/"+search+"?word="+search+"&order=date_d&mode=all&p=1&csw=0&s_mode=s_tag&type=all&lang=en";
		var obj = URLTools.extractAsJson(url).getAsJsonObject();
		logger.info("res={}",obj);
		var arr = obj.get("body").getAsJsonObject().get("illustManga").getAsJsonObject().get("data").getAsJsonArray();
		
		for(var el : arr)
		{
			var wall = new MTGWallpaper();
			wall.setProvider(getName());
			wall.setName(el.getAsJsonObject().get("title").getAsString());
			wall.setAuthor(el.getAsJsonObject().get("userName").getAsString());
			wall.setPublishDate(null);
			wall.setUrl(URI.create(el.getAsJsonObject().get("url").getAsString()));
			el.getAsJsonObject().get("tags").getAsJsonArray().forEach(t->wall.getTags().add(t.getAsString()));
			ret.add(wall);			
		}
		
		
		
		return ret;
		
	}

	@Override
	public String getName() {
		return "Pixiv";
	}

	public static void main(String[] args) {
		new PixivWallpaperProvider().search("lara croft").forEach(w->{
			System.out.println(w.getName() +" " + w.getUrl());
		});;

	}

}

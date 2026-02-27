package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.Date;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.network.RequestBuilder;

import com.google.gson.JsonObject;

public class KonachanWallpaperProvider extends AbstractJsonWallpaperProvider{

	

	@Override
	protected MTGWallpaper parse(JsonObject el) {
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
		
		return pic;
	}

	@Override
	protected RequestBuilder createQuery(String search, int page) {
		return RequestBuilder.build().get().newClient()
				.url("https://konachan.com/post.json")
				.addContent(getPaginationKey(),String.valueOf(page))
				.addContent("limit",String.valueOf(getResultsPerPage()))
				.addContent("tags",search.toLowerCase().replace(" ", "_"));

	}

	@Override
	protected String getPaginationKey() {
		return "page";
	}
	
	@Override
	public String getName() {
		return "Konachan";
	}

}

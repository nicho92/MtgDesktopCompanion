package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.Date;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class YandereWallpaperProvider extends AbstractJsonWallpaperProvider{

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
		pic.addHeader(URLTools.USER_AGENT,MTGConstants.MTG_APP_NAME); 
		for(var s : el.get("tags").getAsString().split(" "))
			pic.getTags().add(s);

		
		return pic;
	}

	@Override
	protected RequestBuilder createQuery(String search, int pidStart) {
		return RequestBuilder.build().newClient().url("https://yande.re/post.json").get()
				.addContent("tags", search.replace(" ", "_"))
				.addContent("limit", String.valueOf(getResultsPerPage()))
				.addContent(getPaginationKey(), String.valueOf(pidStart))
;
	}

	@Override
	protected String getPaginationKey() {
		return "page";
	}
	
	@Override
	public String getName() {
		return "YandeRe";
	}

}

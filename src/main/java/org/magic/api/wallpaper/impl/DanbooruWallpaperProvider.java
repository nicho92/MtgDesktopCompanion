package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class DanbooruWallpaperProvider extends AbstractJsonWallpaperProvider{
	
	private String userAgent = MTGConstants.MTG_APP_NAME+"/1.0";
	

	@Override
	protected MTGWallpaper parse(JsonObject el) {
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
	
	@Override
	protected int getResultsPerPage() {
		return 1000;
	}
	

	@Override
	protected RequestBuilder createQuery(String search, int page) {
		return RequestBuilder.build().get().newClient()
				.url("https://danbooru.donmai.us/posts.json")
				.addContent(getPaginationKey(),String.valueOf(page))
				.addContent("limit",String.valueOf(getResultsPerPage()))
				.addContent("api_key",getAuthenticator().get("API_KEY"))
				.addContent("login",getAuthenticator().get("USER_ID"))
				.addContent("tags",search.replace(" ", "_"))
				.addHeader(URLTools.USER_AGENT, userAgent);
	}

	@Override
	protected String getPaginationKey() {
		return "page";
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY","USER_ID");
	}

	@Override
	public String getName() {
		return "Danbooru";
	}

}

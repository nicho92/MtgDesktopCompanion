package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class KemonoWallpaperProvider extends AbstractJsonWallpaperProvider {

	private static final String BASE_URL="https://kemono.cr";
	
	
	@Override
	public String getName() {
		return "Kemono";
	}

	@Override
	protected int getOffsetSequence() {
		return getResultsPerPage();
	}

	@Override
	protected int getResultsPerPage() {
		return 50;
	}
	
	
	@Override
	protected List<MTGWallpaper> parse(JsonObject obj) {
		
		var ret = new ArrayList<MTGWallpaper>();
		
		for(var att : obj.get("attachments").getAsJsonArray())
		{

			var wall = new MTGWallpaper();
				 wall.setProvider(getName());
				 wall.setPublishDate(UITools.parseDate(obj.get("published").getAsString(), "yyyy-MM-dd'T'HH:mm:ss"));
				 wall.setName(att.getAsJsonObject().get("name").getAsString());
				 wall.setUrlThumb(URI.create("https://img.kemono.cr/thumbnail/data"+att.getAsJsonObject().get("path").getAsString()));
				 wall.setUrl(URI.create("https://img.kemono.cr/thumbnail/data"+att.getAsJsonObject().get("path").getAsString()));
				 wall.setAuthor(obj.get("user").getAsString());
				 wall.setFormat(FilenameUtils.getExtension(wall.getName()));
				 wall.setMature(true);
				 ret.add(wall);
		}
			
		return ret;
	}

	@Override
	protected RequestBuilder createQuery(String search, int pidStart) {
		
		return RequestBuilder.build().get().newClient().url(BASE_URL+"/api/v1/posts")
				.addHeader(URLTools.REFERER, BASE_URL).addHeaders(URLTools.createSecHeaders())
		.addContent("q", search);
	}
	
	@Override
	protected void sleep() {
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	
	@Override
	protected JsonArray extractArrayFromQuery(RequestBuilder req) {
			var json= req.toJson();
			var obj=json.getAsJsonObject();

			if(obj.get("posts")!=null)
				return obj.get("posts").getAsJsonArray();
			else
				return new JsonArray();
		
	}
	

	@Override
	protected String getPaginationKey() {
		return "o";
	}

}

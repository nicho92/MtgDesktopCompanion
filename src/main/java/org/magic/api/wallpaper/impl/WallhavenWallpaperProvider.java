package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractJsonWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.tools.UITools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WallhavenWallpaperProvider extends AbstractJsonWallpaperProvider {

	@Override
	public String getName() {
		return "Wallhaven";
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(LIMIT, MTGProperty.newIntegerProperty("500", "Max results to return", 1, -1), 
				"RESULTS_PER_PAGE", new MTGProperty("24", "Defined pagination in profil","24","32","64"),
				"CATEGORIES", new MTGProperty("GENERAL,ANIME,PEOPLE", "Filter by categories","GENERAL","ANIME","PEOPLE"),
				"PURITY", new MTGProperty("SFW", "Filter by purity","SFW","SKETCHY","NSFW"))
				;
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}

	
	
	protected int getResultsPerPage() {
		return getInt("RESULTS_PER_PAGE");
		
	}

	@Override
	protected MTGWallpaper parse(JsonObject obj) {
		var wall = new MTGWallpaper();
			 wall.setProvider(getName());
			 wall.setName(obj.get("id").getAsString());
			 wall.setMature(obj.get("purity").getAsString().equals("nsfw"));
			 wall.setPublishDate(UITools.parseDate(obj.get("created_at").getAsString(),"yyyy-MM-dd HH:mm:ss"));
			 wall.setFormat(obj.get("file_type").getAsString().substring(obj.get("file_type").getAsString().indexOf("/")+1));
			 wall.setUrl(URI.create(obj.get("path").getAsString()));
			 wall.setUrlThumb(URI.create(obj.get("thumbs").getAsJsonObject().get("small").getAsString()));
			 wall.getTags().add(obj.get("category").getAsString());
			 wall.setAuthor("");
		return wall;
	}

	@Override
	protected JsonArray extractArrayFromQuery(RequestBuilder req) {
		var je = req.toJson();
		
		if (je == null || je.isJsonNull())
			return new JsonArray(0);

		return je.getAsJsonObject().get("data").getAsJsonArray();
	}
	
	
	@Override
	protected RequestBuilder createQuery(String search, int page) {
		
		var catBuilder = new StringBuilder();
		var pBuilder = new StringBuilder();
		
		for(var c : getDefaultAttributes().get("CATEGORIES").getAllowedProperties())
			catBuilder.append(ArrayUtils.contains(getArray("CATEGORIES"),c)?"1":"0");
		
		for(var c : getDefaultAttributes().get("PURITY").getAllowedProperties())
			pBuilder.append(ArrayUtils.contains(getArray("PURITY"),c)?"1":"0");
		
		
		return RequestBuilder.build().get().newClient().url("https://wallhaven.cc/api/v1/search")
				.addContent(getPaginationKey(), String.valueOf(page+1))
				.addContent("q", search)
				.addHeader("X-API-Key", getAuthenticator().get("API_KEY"))
				.addContent("categories", catBuilder.toString())
				.addContent("purity", pBuilder.toString())
				
				;
	}

	@Override
	protected String getPaginationKey() {
		return "page";
	}

}

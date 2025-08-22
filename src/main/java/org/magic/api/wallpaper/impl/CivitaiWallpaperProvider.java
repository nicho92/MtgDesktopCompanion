package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class CivitaiWallpaperProvider extends AbstractWallpaperProvider {
		
	private static final String LIMIT = "LIMIT";
	private final String BASE_URL="https://civitai.com/";
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}
	
	

	@Override
	public String getName() {
		return "CivitAI";
	}
	
	public static void main(String[] args) {
		new CivitaiWallpaperProvider().search("lara croft");
	}
	

	@Override
	public List<MTGWallpaper> search(String search) {
		
		var ret = new ArrayList<MTGWallpaper>();
		
		var obj = RequestBuilder.build().setClient(URLTools.newClient()).get().url(BASE_URL+"/api/v1/models")
				.addHeader("Authorization", "Bearer "+getAuthenticator().get("API_KEY"))
				.addHeader(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON)
				.addContent("limit", getString(LIMIT))
				.addContent("query", search)
				.addContent("nsfw", getString("MATURE")).toJson().getAsJsonObject();
		
		logger.debug("ret = {}", obj);
		
		for(var el : obj.get("items").getAsJsonArray())
		{
			 for(var modelVersion :  el.getAsJsonObject().get("modelVersions").getAsJsonArray())
			 {
				 for(var image : modelVersion.getAsJsonObject().get("images").getAsJsonArray())
				 {
					 if(image.getAsJsonObject().get("type").getAsString().equals("image")) {
						 var wall= new MTGWallpaper();
						 wall.setProvider(getName());
						 wall.setMature(el.getAsJsonObject().get("nsfw").getAsBoolean());
						 wall.setName(el.getAsJsonObject().get("name").getAsString());
						 wall.setAuthor(el.getAsJsonObject().get("creator").getAsJsonObject().get("username").getAsString());
						 el.getAsJsonObject().get("tags").getAsJsonArray().forEach(t->wall.getTags().add(t.getAsString()));
						 wall.setUrl(URI.create(image.getAsJsonObject().get("url").getAsString()));
						 wall.setUrlThumb(wall.getUrl());
						 
						 if(ret.size()<getInt(LIMIT))
							 ret.add(wall);
					 }
				 }
			 }
		}
		logger.info("{} return {} results", getName(),ret.size());
		
		return ret;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("MATURE",MTGProperty.newBooleanProperty(FALSE, "set to true if you want to return mature content"),
								LIMIT,MTGProperty.newIntegerProperty("100", "Max results to return", 1, 200));
	}
	

}

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
		
	private static final String SEARCH_MODE = "SEARCH_MODE";
	private static final String LIMIT = "LIMIT";
	private static final String BASE_URL="https://civitai.com/";
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}
	
	

	@Override
	public String getName() {
		return "CivitAI";
	}
	
	@Override
	public List<MTGWallpaper> search(String search) {
		var ret = new ArrayList<MTGWallpaper>();
		
		var page=1;
		
		while(ret.size()<getInt(LIMIT))
		{
		
			var build = RequestBuilder.build().newClient().get().url(BASE_URL+"/api/v1/models")
					.addHeader("Authorization", "Bearer "+getAuthenticator().get("API_KEY"))
					.addHeader(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON)
					.addContent(getString(SEARCH_MODE), search)
					.addContent("limit", "100")
					.addContent("nsfw", getString("MATURE"));
					
			
			if(getString(SEARCH_MODE).equals("tag"))
				build = build.addContent("page", String.valueOf(page++));
			
			
			var obj = build.toJson().getAsJsonObject();
			
			logger.debug("ret = {}", obj);
			
		
			if(obj.get("error")!=null)
			{
				logger.error("error : {}", obj.get("error").getAsString());
				break;
			}
			
		
			if(obj.get("items").getAsJsonArray().isEmpty())
				break;
		
		
			for(var el : obj.get("items").getAsJsonArray())
			{
				 for(var modelVersion :  el.getAsJsonObject().get("modelVersions").getAsJsonArray())
				 {
					 for(var image : modelVersion.getAsJsonObject().get("images").getAsJsonArray())
					 {
						 if(image.getAsJsonObject().get("type").getAsString().equals("image")) 
						 {
							 var wall= new MTGWallpaper();
								 wall.setProvider(getName());
								 wall.setMature(image.getAsJsonObject().get("nsfwLevel").getAsInt()>1);
								 wall.setName(el.getAsJsonObject().get("name").getAsString() +"_"+image.getAsJsonObject().get("id").getAsString());
								 wall.setUrl(URI.create(image.getAsJsonObject().get("url").getAsString()));
								 wall.setUrlThumb(URI.create(wall.getUrl().toASCIIString().replaceAll("width=\\d+", "width=30")));
								 wall.setPublishDate(Date.from(Instant.parse(modelVersion.getAsJsonObject().get("publishedAt").getAsString())));
								 el.getAsJsonObject().get("tags").getAsJsonArray().forEach(t->wall.getTags().add(t.getAsString()));
								 try {
								 	 wall.setAuthor(el.getAsJsonObject().get("creator").getAsJsonObject().get("username").getAsString());
								 }
								 catch(NullPointerException _)
								 {
									 wall.setAuthor("");
								 }
							
							if(ret.size()>=getInt(LIMIT))
							 {
								 logger.info("{} return {} results", getName(),ret.size());
								 return ret;
							 }
							 else
							 {
								 ret.add(wall);
							 }
						 }
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
							  LIMIT,MTGProperty.newIntegerProperty("250", "Max results to return", 1, -1),
							  SEARCH_MODE,new MTGProperty("tag", "set search mode by tags or by query", "tag","query")
								);
	}
	

}

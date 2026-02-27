 package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;

public class R34WallPaperProvider extends AbstractWallpaperProvider{

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY","USER_ID");
	}
	
	@Override
	public List<MTGWallpaper> search(String search) {
		var results = new ArrayList<MTGWallpaper>();
		var pidStart=0;
		
		var req = RequestBuilder.build().newClient().url("https://api.rule34.xxx/index.php").get()
														.addContent("page", "dapi")
														.addContent("tags", search.replace(" ", "_"))
														.addContent("limit", "1000")
														.addContent("pid", String.valueOf(pidStart))
														.addContent("json","1")
														.addContent("s","post")
														.addContent("q", "index")
														.addContent("api_key", getAuthenticator().get("API_KEY"))
														.addContent("user_id", getAuthenticator().get("USER_ID"));
		
		while(results.size()<getInt("LIMIT"))
		{
				
				var ret = req.toJson();
				logger.trace("return : {}",  ret);
				
				
				if(ret==null || ret.isJsonNull())
					return returnResults(results); 
				
				var arr = ret.getAsJsonArray();
				
				if(arr.isEmpty())
					return returnResults(results);
				
				for(var e : arr)
				{
					try {
						var obj = e.getAsJsonObject();
						var wall = new MTGWallpaper();  
						
						wall.setProvider(getName());
						wall.setAuthor(obj.get("owner").getAsString());
						wall.setMature(obj.get("rating").getAsString().equalsIgnoreCase("explicit"));
						wall.setName(obj.get("id").getAsString());
						wall.setUrl(URI.create(obj.get("file_url").getAsString()));
						wall.setUrlThumb(URI.create(obj.get("preview_url").getAsString()));
						wall.setFormat(obj.get("image").getAsString().substring(obj.get("image").getAsString().indexOf(".")+1));
						wall.setPublishDate(new Date(obj.get("change").getAsLong()*1000));
						Stream.of(obj.get("tags").getAsString().split(" ")).forEach(wall.getTags()::add);
						
						if(!wall.getFormat().endsWith("mp4") && !CollectionUtils.containsAny(wall.getTags(),getList("FILTER"))) {
							results.add(wall);
							notify(wall);
						}
						
						if(results.size()>=getInt("LIMIT"))
							return returnResults(results);

					
					} catch (Exception ex) {
						logger.error("Error getting wall for {} : {}",e,ex.getMessage());
					}
				}
				req = req.updateContent("pid",String.valueOf(pidStart++));
		}
		return returnResults (results);
		
	}
	
	private List<MTGWallpaper> returnResults(List<MTGWallpaper> results) {
		logger.info("Return {} results",results.size());
		return results;
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",MTGProperty.newIntegerProperty("500", "Max results to return", 1, -1), "FILTER",new MTGProperty("", "don't return results with this comma separated tags"));
	}
	

	@Override
	public String getName() {
		return "Rule34";
	}

}

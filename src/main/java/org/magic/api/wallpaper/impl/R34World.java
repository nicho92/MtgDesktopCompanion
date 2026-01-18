 package org.magic.api.wallpaper.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class R34World extends AbstractWallpaperProvider{

	@Override
	public List<MTGWallpaper> search(String search) {

		var results=new ArrayList<MTGWallpaper>();
		var client = URLTools.newClient();
		var total = 100; 
		var skip=0;
		
		try {
			while(results.size()<getInt("LIMIT"))
			{
				var ret = client.doPost("https://rule34.world/api/v2/post/search/root", new StringEntity("{\"skip\":"+skip+",\"take\":"+total+",\"countTotal\":true,\"checkHasMore\":true,\"type\":0,\"filterAi\":false,\"sortBy\":0,\"includeTags\":[\""+search.replace(" ", "_")+"\"]}"),Map.of(URLTools.CONTENT_TYPE,"application/json"));
				var content = URLTools.toText(ret.getEntity().getContent());
				
				logger.debug("return : {}",content);
				var res = URLTools.toJson(content).getAsJsonObject();
				
				if(res.get("items")==null || res.get("items").getAsJsonArray().isEmpty())
					return results;
				
				for(var it : res.get("items").getAsJsonArray())
				{
					
					if(it.getAsJsonObject().get("type").getAsInt()==0) 
					{
						var wall = new MTGWallpaper();
						var id = it.getAsJsonObject().get("id").getAsString();
						var img = "https://rule34storage.b-cdn.net/posts/"+id.substring(0,4)+"/"+id+"/"+id;
						
						wall.setFormat("jpg");
						wall.setUrlThumb(URI.create(img+".picpreview.jpg"));
						wall.setUrl(URI.create(img+".pic.jpg"));
						wall.setName(id);
						wall.setAuthor("");
						wall.setProvider(getName());
						wall.setMature(true);
						wall.setPublishDate(UITools.parseGMTDate(it.getAsJsonObject().get("created").getAsString()));
						results.add(wall);
						
						if(results.size()>=getInt("LIMIT"))
						{
							logger.info("{} return {} results", getName(), results.size());
							return results;
						}
					}
				}
				skip+=total;
				
			}
			
		} catch (Exception e) {
			logger.error("Error with ",e);
		}
		return results;
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",MTGProperty.newIntegerProperty("150", "Max results to return", 1, -1));
	}
	

	@Override
	public String getName() {
		return "R34World";
	}

}

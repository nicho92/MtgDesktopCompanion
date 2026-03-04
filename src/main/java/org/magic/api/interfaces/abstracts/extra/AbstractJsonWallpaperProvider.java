package org.magic.api.interfaces.abstracts.extra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractJsonWallpaperProvider extends AbstractWallpaperProvider {

	

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LIMIT",MTGProperty.newIntegerProperty("500", "Max results to return", 1, -1), 
							 "FILTER",new MTGProperty("", "don't return results with this comma separated tags")
							);
	}

	protected abstract MTGWallpaper parse(JsonObject obj);
	protected abstract RequestBuilder createQuery(String search, int pidStart);
	protected abstract String getPaginationKey();
	
	
	protected int getResultsPerPage() 
	{
		return 100;
	}
	
	protected JsonArray extractArrayFromQuery(RequestBuilder req)
	{
		var je = req.toJson();
		
		if(je==null || je.isJsonNull())
			return new JsonArray(0);
	
		return je.getAsJsonArray();
	}

	@Override
	public List<MTGWallpaper> search(String search) {
		var results = new ArrayList<MTGWallpaper>();
		var pidStart=0;
		var req = createQuery(search, pidStart);
		
		
		var filtered = 0;
		
		while(results.size()<getInt("LIMIT"))
		{
				var arr = extractArrayFromQuery(req);
		
				if(arr.isEmpty())
					break;
				
				filtered = 0;
				for(var e : arr)
				{
					try {
						var wall = parse(e.getAsJsonObject());
						if(!wall.getFormat().endsWith("mp4") && !CollectionUtils.containsAny(wall.getTags(),getList("FILTER"))) 
						{
							results.add(wall);
							notify(wall);
						}
						else
						{
							filtered++;
						}
						
						if(results.size()>=getInt("LIMIT"))
							break;
					
					} catch (Exception ex) {
						logger.error("Error getting wall for {} : {}",e,ex.getMessage());
					}
				}
				logger.debug("read {}={}. return {} items. Filtered={} . Complete ={}/{}.",getPaginationKey(),pidStart,arr.size() ,filtered,results.size(),getInt("LIMIT"));

				
				if(arr.size()<getResultsPerPage())
					break;
							
				pidStart = pidStart+1;
				
				req = req.updateContent(getPaginationKey(),String.valueOf(pidStart));
				
		}
		
		
		logger.info("Return {} results",results.size());
		return results;
		
	}
	
	
	
	
	
}

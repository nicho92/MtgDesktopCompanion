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

	protected List<MTGWallpaper> returnResults(List<MTGWallpaper> results) {
		logger.info("Return {} results",results.size());
		return results;
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
		
		
		return req.toJson().getAsJsonArray();
	}
	
	
	
	
	@Override
	public List<MTGWallpaper> search(String search) {
		var results = new ArrayList<MTGWallpaper>();
		var pidStart=0;
		var req = createQuery(search, pidStart);
		
		
		while(results.size()<getInt("LIMIT"))
		{
				logger.trace("read at {}={} complete ={}/{}",getPaginationKey(),pidStart,results.size(),getInt("LIMIT"));
				var arr = extractArrayFromQuery(req);
				
				if(arr.isEmpty())
					break;
				
				for(var e : arr)
				{
					try {
						var wall = parse(e.getAsJsonObject());
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
				
				if(arr.size()<getResultsPerPage())
					break;
				
				req = req.updateContent(getPaginationKey(),String.valueOf(pidStart++));
		}
		return returnResults (results);
		
	}
	
	
	
	
	
}

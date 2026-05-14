package org.magic.api.interfaces.abstracts.extra;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;
import org.magic.services.network.RequestBuilder;

public abstract class AbstractJsonWallpaperProvider extends AbstractWallpaperProvider {

	protected static final String LIMIT = "LIMIT";

	private Set<String> rejectExt=Set.of("mp4", "zip", "bin", "psd", "rar", "pdf", "docx", "mp3");
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of(LIMIT, MTGProperty.newIntegerProperty("500", "Max results to return", 1, -1), "FILTER",
				new MTGProperty("", "don't return results with this comma separated tags"));
	}
	

	protected abstract List<MTGWallpaper> parse(JsonObject obj);
	protected abstract RequestBuilder createQuery(String search, int pidStart);
	protected abstract String getPaginationKey();

	protected int getResultsPerPage() {
		return 100;
	}
	
	protected int getOffsetSequence()
	{
		return 1;
	}
	

	protected JsonArray extractArrayFromQuery(RequestBuilder req) {
		var je = req.toJson();

		if (je == null || je.isJsonNull())
			return new JsonArray(0);

		return je.getAsJsonArray();
	}

	@Override
	public List<MTGWallpaper> search(String search) {
		var results = new ArrayList<MTGWallpaper>();
		var pidStart = 0;
		var req = createQuery(search, pidStart);

		var filtered = 0;

		while (results.size() < getInt(LIMIT)) {
			var arr = extractArrayFromQuery(req);

			if (arr.isEmpty())
				break;

			filtered = 0;
			for (var e : arr) {
				try {
					
					for(var wall : parse(e.getAsJsonObject()))
					{
						if (!isBinary(wall.getFormat())&& !CollectionUtils.containsAny(wall.getTags(), getList("FILTER"))) 
						{
							results.add(wall);
							notify(wall);
						} else {
							filtered++;
						}
					}
					
					if (results.size() >= getInt(LIMIT))
						break;
					
				} catch (Exception ex) {
					logger.error("Error getting wall for {} : {}", e, ex.getMessage());
				}
			}
			logger.debug("read {}={}. return {} items. Filtered={} . Complete ={}/{}.", getPaginationKey(), pidStart,
					arr.size(), filtered, results.size(), getInt(LIMIT));

			if (arr.size() < getResultsPerPage())
				break;

			pidStart = pidStart + getOffsetSequence();

			req = req.updateContent(getPaginationKey(), String.valueOf(pidStart));
			
			sleep();
			
			
		}

		logger.info("Return {} results", results.size());
		return results;

	}

	
	private boolean isBinary(String format) {
		return rejectExt.stream().anyMatch(format::endsWith);
	}

	protected void sleep()
	{
		//do nothing by default
	}

	

}

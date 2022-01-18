package org.magic.services.providers;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.magic.api.beans.audit.Location;
import org.magic.services.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.tools.TCache;

import com.google.gson.JsonObject;

public class IPTranslator {

	private TCache<Location> cache;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	
	public IPTranslator() {
		cache = new TCache<>("ips");
	}
	
	
	public Location getLocationFor(String ip)
	{
		try {
			return cache.get(ip, new Callable<Location>() {
				
				@Override
				public Location call() throws Exception {
					var o = URLTools.extractAsJson("https://ipapi.co/"+ip+"/json").getAsJsonObject();
					return translate(o);
					
				}
			});
		} catch (ExecutionException e) {
			logger.error(e);
			return null;
		}
	}
	

	private Location translate(JsonObject o) {
		var loc = new Location();
		
		if(o.get("error")!=null)
		{
			logger.error(o.get("ip") + " " + o.get("reason"));
			return loc;
		}
		logger.debug("parsing " + o);
			loc.setCity(o.get("city").getAsString());
			loc.setContinentCode(o.get("continent_code").getAsString());
			loc.setRegion(o.get("region").getAsString());
			loc.setCountry(o.get("country").getAsString());
			loc.setCountryName(o.get("country_name").getAsString());
			loc.setCountryCode(o.get("country_code").getAsString());
			loc.setLatitude(o.get("latitude").getAsDouble());
			loc.setLongitude(o.get("longitude").getAsDouble());
			loc.setTimezone(o.get("timezone").getAsString());
			loc.setCountryArea(o.get("country_area").getAsDouble());
			loc.setOperator(o.get("org").getAsString());
			return loc;
			
			
	}
	
	
}

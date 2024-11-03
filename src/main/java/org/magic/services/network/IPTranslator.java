package org.magic.services.network;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.audit.Location;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.TCache;

import com.google.common.net.InetAddresses;
import com.google.gson.JsonObject;

public class IPTranslator {

	private TCache<Location> cache;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static IPTranslator inst;

	
	
	public static IPTranslator getInstance()
	{
		if(inst ==null)
			inst = new IPTranslator();
		
		return inst;
	}
	
	private IPTranslator() {
		cache = new TCache<>("ips");
	}

	public Location getLocationFor(String ip)
	{

		try {
			var inaddr = InetAddresses.forString(ip);
			if(inaddr.isAnyLocalAddress() || inaddr.isLoopbackAddress() || inaddr.isSiteLocalAddress())
				return null;

			return cache.get(ip, new Callable<Location>() {
				@Override
				public Location call() throws Exception {
					
					var o = RequestBuilder.build().url("http://ip-api.com/json/"+ip+"?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp").setClient(URLTools.newClient()).get().toJson();
					return translate(o.getAsJsonObject());
				}
			});
		}
		catch(IllegalArgumentException e)
		{
			return null;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}


	private Location translate(JsonObject o) {
		var loc = new Location();

		if(o.get("error")!=null)
		{
			logger.error("error getting IP={}",o);
			loc.setAll("Unknow");
			return loc;
		}
			logger.debug("parsing {}",o);
			loc.setCity(o.get("city").getAsString());
			loc.setContinentCode(o.get("continentCode").getAsString());
			loc.setRegion(o.get("regionName").getAsString());
			loc.setCountry(o.get("country").getAsString());
			loc.setCountryCode(o.get("countryCode").getAsString());
			loc.setLatitude(o.get("lat").getAsDouble());
			loc.setLongitude(o.get("lon").getAsDouble());
			loc.setTimezone(o.get("timezone").getAsString());
			loc.setOperator(o.get("isp").getAsString());
			return loc;


	}


}

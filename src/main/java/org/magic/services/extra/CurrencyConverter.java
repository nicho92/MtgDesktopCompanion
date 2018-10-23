package org.magic.services.extra;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Currency;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CurrencyConverter {
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private HashMap<String, Double> map;
	private File cache;
	private String token;
	
	public CurrencyConverter(String token) {
		this.token=token;
		map = new HashMap<>();
		cache=new File(MTGConstants.CONF_DIR,"conversionData.json");
		init();
	}
	
	public Double convert(Currency from, Currency to, double value)
	{
		return convert(from.getCurrencyCode(), to.getCurrencyCode(), value);
	}
	
	public Double convert(String from, String to, double value)
	{
		try {
			if(!from.equalsIgnoreCase("USD")&&!to.equalsIgnoreCase("USD"))
				return usdConvert("USD", to, 1)*usdConvert(from, "USD", 1)*value;
			else
				return usdConvert(from, to, value);
		}
		catch(Exception e)
		{
			logger.error("Error convert " + from + " to " + to +", return default value");
			return value;
		}
			
	}
	
	public void clean() throws IOException
	{
		FileUtils.forceDelete(cache);
		init();
	}
	
	private Double usdConvert(String from, String to, double value) {
		
		double ret = 0;
		
		if(from.equals("USD"))
		{
			ret = (value * map.get(to));
		}
		else if(to.equals("USD"))
		{
		ret = value / map.get(from);
		}
		return ret;
		
	}
	
	public void init() {
		try {
			JsonObject obj;
			if(!cache.exists())
			{
				
				logger.debug(cache.getAbsolutePath() + " doesn't exist. Will create it from website");
				JsonElement parse = new JsonParser().parse(new InputStreamReader(URLTools.openConnection("http://apilayer.net/api/live?access_key="+token).getInputStream()));
				obj = parse.getAsJsonObject().get("quotes").getAsJsonObject();
				FileUtils.writeStringToFile(cache, obj.toString(), MTGConstants.DEFAULT_ENCODING);
				logger.debug(cache.getAbsolutePath() + " created");
			}
			else
			{
				obj = new JsonParser().parse(FileUtils.readFileToString(cache,MTGConstants.DEFAULT_ENCODING)).getAsJsonObject();
			}
			
			obj.entrySet().forEach(entry->map.put(entry.getKey().substring(3),entry.getValue().getAsDouble()));
		}
		catch(Exception e)
		{
			logger.error("couldn't init CurrencyConverter");
		}
		
	}
	
	
	
	
	
}

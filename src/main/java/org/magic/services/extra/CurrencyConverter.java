package org.magic.services.extra;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CurrencyConverter {
	private Logger logger = MTGLogger.getLogger(CurrencyConverter.class);
	private HashMap<String, Double> map;
	private File cache;

	public CurrencyConverter() {
		map = new HashMap<>();
		cache=new File(MTGConstants.CONF_DIR,"conversionData.json");
		try {
			init();
		} catch (IOException e) {
			logger.error("Could not init Currency Converter",e);
		}
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
	
	public void init() throws IOException{
		JsonObject obj;
		
		if(!cache.exists())
		{
			String token = MTGControler.getInstance().get("currencylayer-access-api");
			URL url = new URL("http://apilayer.net/api/live?access_key="+token);
			logger.debug(cache.getAbsolutePath() + " doesn't exist. Will create it from "+url);
			JsonElement parse = new JsonParser().parse(new InputStreamReader(url.openStream()));
			obj = parse.getAsJsonObject().get("quotes").getAsJsonObject();
			FileUtils.writeStringToFile(cache, obj.toString(), MTGConstants.DEFAULT_ENCODING);
		}
		else
		{
			obj = new JsonParser().parse(FileUtils.readFileToString(cache,MTGConstants.DEFAULT_ENCODING)).getAsJsonObject();
		}
		
		obj.entrySet().forEach(entry->{
			map.put(entry.getKey().substring(3),entry.getValue().getAsDouble());
		});
	}
	
	
	
	
	
}

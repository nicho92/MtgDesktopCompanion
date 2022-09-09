package org.magic.services.providers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.tools.FileTools;

import com.google.gson.JsonObject;


public class ApilayerCurrencyConverter {
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private HashMap<String, Double> map;
	private File cache;
	private String token;
	
	public ApilayerCurrencyConverter(String token) {
		this.token=token;
		map = new HashMap<>();
		cache=Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"conversionData.json").toFile();
	}
	
	public Currency getCurrentCurrency()
	{
		
		if(!MTGControler.getInstance().get("currency").isEmpty())
			return Currency.getInstance(MTGControler.getInstance().get("currency"));
		else
			return Currency.getInstance(MTGControler.getInstance().getLocale());
	}
	
	public Double convertTo(Currency from, Double value)
	{
		if(value==null)
			return 0.0;
		
		return convert(from.getCurrencyCode(),getCurrentCurrency().getCurrencyCode(), value);
	}
	
	public Date getCurrencyDateCache()
	{
		if(cache.exists())
			return new Date(cache.lastModified());
		else
			return null;
	}
	
	public Double convert(Currency from, Currency to, double value)
	{
		return convert(from.getCurrencyCode(), to.getCurrencyCode(), value);
	}
	
	public Double convert(String from, String to, double value)
	{
		double ret=0;
		
		if(from.equalsIgnoreCase(to))
			return value;
		
		
		try {
			if(!from.equalsIgnoreCase("USD")&&!to.equalsIgnoreCase("USD"))
				ret= usdConvert("USD", to, 1)*usdConvert(from, "USD", 1)*value;
			else
				ret= usdConvert(from, to, value);
	
			return ret;
		}
		catch(Exception e)
		{
			logger.error("Error convert " + from + " to " + to +", return default value",e);
			return value;
		}
		
		
			
	}
	
	public void clean() throws IOException
	{
		FileTools.deleteFile(cache);
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
	
	public Map<String,Double> getChanges()
	{
		return map;
	}
	
	
	public boolean isEnable() {
		return MTGControler.getInstance().get("currencylayer-converter-enable").equals("true");
	}
	
	public void init() throws IOException {
			JsonObject obj = new JsonObject();
			map.clear();
			if(!cache.exists() && !token.isEmpty())
			{
				
				logger.debug(cache.getAbsolutePath() + " doesn't exist. Will create it from website");
				var parse = URLTools.extractAsJson("http://apilayer.net/api/live?access_key="+token);
				obj = parse.getAsJsonObject().get("quotes").getAsJsonObject();
				FileTools.saveFile(cache, obj.toString());
				logger.debug(cache.getAbsolutePath() + " created");
			}
			else //if(!token.isEmpty())
			{
				obj = FileTools.readJson(cache).getAsJsonObject();
			}
			obj.entrySet().forEach(entry->map.put(entry.getKey().substring(3),entry.getValue().getAsDouble()));
		
	}
	
	
	
	
	
}

package org.beta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Currency;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;
import org.magic.tools.IDGenerator;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class CurrencyConverter {
	private Logger logger = MTGLogger.getLogger(CurrencyConverter.class);
	
	
	public static void main(String[] args) throws IOException {
		System.out.println(new CurrencyConverter().getValue(Currency.getInstance("EUR"), Currency.getInstance("USD"), 1));
	}
	
	public void refresh()
	{
		
	}
	
	
	
	public double getValue(String from, String to, double value) throws IOException
	{
		return getValue(Currency.getInstance(from), Currency.getInstance(to), value);
	}
	
	
	public double getValue(Currency from, Currency to, double value) throws IOException
	{
		
		String code = from.getCurrencyCode() +"_"+to.getCurrencyCode();
		
		StringBuilder build = new StringBuilder();
		build.append("https://free.currencyconverterapi.com/api/v5/convert?q=")
			 .append(code)
			 .append("&compact=ultra");
		
		logger.debug("get currency " + build);
		
		JsonElement el = new JsonParser().parse(new InputStreamReader(new URL(build.toString()).openStream()));
		
		return value*el.getAsJsonObject().get(code).getAsDouble();
		
	}
	
	
	
	
}

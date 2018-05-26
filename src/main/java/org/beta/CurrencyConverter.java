package org.beta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;


public class CurrencyConverter {
	private Logger logger = MTGLogger.getLogger(CurrencyConverter.class);

	private HashMap<Currency, Double> map;
	
	public CurrencyConverter() {
		map = new HashMap<>();
		map.put(Currency.getInstance("USD"), 1.0);
		map.put(Currency.getInstance("EUR"), 0.857889711);
		map.put(Currency.getInstance("GBP"), 1.330805);
	}
	
	public static void main(String[] args){
		System.out.println(new CurrencyConverter().getValue(Currency.getInstance("USD"), Currency.getInstance("EUR"), 1));
	}
	
	public double getValue(String from, String to, double value)
	{
		return getValue(Currency.getInstance(from), Currency.getInstance(to), value);
	}
	
	
	public double getValue(Currency from, Currency to, double value)
	{
		return 0;
	}
	
	
	
	
}

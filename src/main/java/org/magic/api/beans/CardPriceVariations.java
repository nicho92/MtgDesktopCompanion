package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Iterables;

public class CardPriceVariations {

	private Map<Date,Double> variations;
	
	public CardPriceVariations() {
		variations = new TreeMap<>();
	}
	
	private Date getLastValueAt(int val)
	{
		List<Entry<Date, Double>> res = asList();
		return res.get(res.size()-val).getKey();
	}
	
	public Date getLastWeek()
	{
		return getLastValueAt(7);
	}
	
	public Date getYesterday()
	{
		return getLastValueAt(2);
	}
	
	public Date getLastDay()
	{
		return Iterables.getLast(variations.keySet());
	}
	
	public List<Entry<Date, Double>> asList()
	{
		return new ArrayList<>(entrySet());
	}
	
	
	public void put(Date date,Double p)
	{
		variations.put(date, p);
	}
	
	
	public Map<Date, Double> getVariations() {
		return variations;
	}
	
	public Double get(Date d)
	{
		return variations.get(d);
	}
	
	
	public Collection<Double> values()
	{
		return variations.values();
	}
	
	public Set<Entry<Date, Double>> entrySet()
	{
		return variations.entrySet();
	}
	
	
}

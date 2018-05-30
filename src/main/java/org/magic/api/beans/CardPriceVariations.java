package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CardPriceVariations {

	private Map<Date,Double> variations;
	private MagicCard card;
	
	
	public CardPriceVariations() {
		variations = new TreeMap<>();
	}
	
	private Date getLastValueAt(int val)
	{
		if(variations.isEmpty())
			return null;
		
		List<Entry<Date, Double>> res = asList();
		return res.get(res.size()-val).getKey();
	}
	
	public CardShake toCardShake()
	{
		
		if(!variations.isEmpty())
		{
			Date now = getLastDay();
			Date yesterday = getYesterday();
			Date week = getLastWeek();
	
			double valDay = get(now) - get(yesterday);
			double valWeek = get(now) - get(week);		 
			double pcWeek = (get(now) - get(week))/get(week)*100;
			double pcDay = (get(now) - get(yesterday))/get(yesterday)*100;
			
			CardShake cs = new CardShake();
					  cs.setCard(card);
					  cs.setName(cs.getCard().getName());
					  
			cs.setEd(cs.getCard().getCurrentSet().getSet());
			cs.setDateUpdate(new Date());
			cs.setPercentDayChange(pcDay);
			cs.setPercentWeekChange(pcWeek);
			cs.setPriceDayChange(valDay);
			cs.setPriceWeekChange(valWeek);
			cs.setPrice(get(now));
			return cs;
		}
		
		return null;
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
		return getLastValueAt(1);
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

	public void setCard(MagicCard mc) {
		this.card=mc;
		
	}
	
	public MagicCard getCard() {
		return card;
	}
	
	
}

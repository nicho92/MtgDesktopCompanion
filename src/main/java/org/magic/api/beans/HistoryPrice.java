package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class HistoryPrice<T> implements Iterable<Map.Entry<Date,Double>> {

	private Map<Date,Double> variations;
	private T pack;
	private Currency currency;
	private boolean foil;
	private String serieName;
	private String support;


	public HistoryPrice(T pack) {
		this.pack=pack;
		this.foil=false;
		variations = new TreeMap<>();
		currency=Currency.getInstance("USD");
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}


	public void setSerieName(String s) {
		this.serieName=s;
	}

	public String getSerieName() {
		return serieName;
	}

	@Override
	public String toString() {
		return (pack + ((foil)?"(foil)":""));

	}

	public T getItem() {
		return pack;
	}

	public boolean isEmpty()
	{
		return variations.isEmpty();
	}



	public boolean isFoil() {
		return foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}

	private Date getLastValueAt(int val)
	{
		if(isEmpty())
			return null;

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
		return getLastValueAt(1);
	}

	public Entry<Date, Double> getHigher()
	{
		return Collections.max(variations.entrySet(), (Entry<Date,Double> e1, Entry<Date,Double> e2) -> e1.getValue().compareTo(e2.getValue()));
	}

	public Entry<Date, Double> getLower()
	{
		return Collections.min(variations.entrySet(), (Entry<Date,Double> e1, Entry<Date,Double> e2) -> e1.getValue().compareTo(e2.getValue()));
	}

	public Double getLastValue()
	{
		return variations.get(getLastDay());
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
		try {
		return variations.get(d);
		}
		catch(Exception e)
		{
			return 0.0;
		}
	}


	public Collection<Double> values()
	{
		return variations.values();
	}

	public Set<Entry<Date, Double>> entrySet()
	{
		return variations.entrySet();
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public Iterator<Entry<Date, Double>> iterator() {
		return variations.entrySet().iterator();
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
			var cs = new CardShake();

			if(pack instanceof MTGCard mc) {
				cs.setCard(mc);
				cs.setName(cs.getCard().getName());
				cs.setEd(cs.getCard().getCurrentSet().getSet());
			}
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


}

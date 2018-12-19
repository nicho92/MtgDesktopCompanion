package org.magic.sorters;

import java.util.Comparator;

import org.magic.api.beans.CardShake;

public class PricesCardsShakeSorter implements Comparator<CardShake>  {

	public enum SORT {DAY_PRICE_CHANGE, DAY_PERCENT_CHANGE,PRICE}


	private SORT t;
	
	public PricesCardsShakeSorter() {
		this.t = SORT.PRICE;
	}
	
	
	public PricesCardsShakeSorter(SORT t) {
		this.t = t;
	}
	
	@Override
	public int compare(CardShake o1, CardShake o2) {
		double val1 = getValFor(o1);
		double val2 = getValFor(o2);
		
		return Double.compare(val1, val2);
	}
	
	
	private double getValFor(CardShake cs)
	{
		switch (t) {
			case DAY_PERCENT_CHANGE:return cs.getPercentDayChange();
			case DAY_PRICE_CHANGE: return cs.getPriceDayChange();
			case PRICE: return cs.getPrice();
			default : return 0;
		}
	}
	
}

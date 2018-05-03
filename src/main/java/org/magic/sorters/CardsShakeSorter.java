package org.magic.sorters;

import java.util.Comparator;

import org.magic.api.beans.CardShake;

public class CardsShakeSorter implements Comparator<CardShake>  {

	public enum SORT {DAY_PRICE_CHANGE, DAY_PERCENT_CHANGE}


	private SORT t;
	
	public CardsShakeSorter(SORT t) {
		this.t = t;
		
	}
	
	@Override
	public int compare(CardShake o1, CardShake o2) {
		double val1 = getValFor(o1);
		double val2 = getValFor(o2);
		
		
		if (val1 > val2)
			return -1;

		if (val1 <val2)
			return 1;

		return 0;
	}
	
	
	private double getValFor(CardShake cs)
	{
		switch (t) {
			case DAY_PERCENT_CHANGE:return cs.getPercentDayChange();
			case DAY_PRICE_CHANGE: return cs.getPriceDayChange();
			default : return 0;
		}
	}
	
}

package org.magic.api.sorters;

import java.util.Comparator;

import org.magic.api.beans.MTGPrice;

public class MagicPricesComparator implements Comparator<MTGPrice> {

	@Override
	public int compare(MTGPrice o1, MTGPrice o2) {

		if (o1.getValue() < o2.getValue())
			return -1;

		if (o1.getValue() > o2.getValue())
			return 1;

		return 0;
	}

}

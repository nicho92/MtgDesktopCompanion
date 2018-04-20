package org.magic.sorters;

import java.util.Comparator;

import org.magic.api.beans.MagicPrice;

public class MagicPricesComparator implements Comparator<MagicPrice> {

	@Override
	public int compare(MagicPrice o1, MagicPrice o2) {

		if (o1.getValue() < o2.getValue())
			return -1;

		if (o1.getValue() > o2.getValue())
			return 1;

		return 0;
	}

}

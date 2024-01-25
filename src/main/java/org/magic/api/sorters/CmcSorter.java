package org.magic.api.sorters;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGComparator;

public class CmcSorter implements MTGComparator<MTGCard> {

	@Override
	public int compare(MTGCard o1, MTGCard o2) {
		if (o1.getCmc() < o2.getCmc())
			return -1;

		if (o1.getCmc() > o2.getCmc())
			return 1;

		return 0;
	}

	@Override
	public int getWeight(MTGCard mc) {
		return mc.getCmc() + 1;
	}

	@Override
	public String toString() {
		return "Cmc Sorter";
	}

}

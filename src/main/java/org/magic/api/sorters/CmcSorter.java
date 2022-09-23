package org.magic.api.sorters;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComparator;

public class CmcSorter implements MTGComparator<MagicCard> {

	@Override
	public int compare(MagicCard o1, MagicCard o2) {
		if (o1.getCmc() < o2.getCmc())
			return -1;

		if (o1.getCmc() > o2.getCmc())
			return 1;

		return 0;
	}

	@Override
	public int getWeight(MagicCard mc) {
		return mc.getCmc() + 1;
	}

	@Override
	public String toString() {
		return "Cmc Sorter";
	}

}

package org.magic.api.sorters;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.extra.MTGComparator;

public class CardNameSorter implements MTGComparator<MTGCard> {


	@Override
	public String toString() {
		return "Name Sorter";
	}

	@Override
	public int compare(MTGCard mc1, MTGCard mc2) {

		return mc1.getName().compareTo(mc2.getName());
	}

	@Override
	public int getWeight(MTGCard mc) {

		return mc.getName().hashCode();

	}

}

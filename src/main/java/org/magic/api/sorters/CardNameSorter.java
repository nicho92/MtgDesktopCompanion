package org.magic.api.sorters;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComparator;

public class CardNameSorter implements MTGComparator<MagicCard> {


	@Override
	public String toString() {
		return "Name Sorter";
	}

	@Override
	public int compare(MagicCard mc1, MagicCard mc2) {

		return mc1.getName().compareTo(mc2.getName());
	}

	@Override
	public int getWeight(MagicCard mc) {

		return mc.getName().hashCode();

	}

}

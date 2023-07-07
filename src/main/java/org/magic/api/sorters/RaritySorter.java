package org.magic.api.sorters;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComparator;

public class RaritySorter implements MTGComparator<MagicCard> {

	@Override
	public String toString() {
		return "Type Sorter";
	}

	@Override
	public int compare(MagicCard mc1, MagicCard mc2) {

		if (getWeight(mc1) < getWeight(mc2))
			return -1;

		if (getWeight(mc1) == getWeight(mc2))
			return 0;

		return 1;
	}

	@Override
	public int getWeight(MagicCard mc) {
		switch(mc.getRarity())
		{

		case COMMON: return 0;
		case UNCOMMON:return 1;
		case RARE: return 2;
		case MYTHIC: return 3;
		case SPECIAL: return 4;
		case TIMESHIFTED: return 5;
		case BONUS: return 6;
		default : return 0;
		}
		
	}

}

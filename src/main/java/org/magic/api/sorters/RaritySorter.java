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

		case COMMON: return 1;
		case UNCOMMON:return 2;
		case RARE: return 3;
		case MYTHIC: return 4;
		case SPECIAL: return 5;
		case TIMESHIFTED: return 6;
		case BONUS: return 7;
		default : return 1;
		}
		
	}

}

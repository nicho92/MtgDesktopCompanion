package org.magic.api.sorters;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGComparator;

public class RaritySorter implements MTGComparator<MTGCard> {

	@Override
	public String toString() {
		return "Type Sorter";
	}

	@Override
	public int compare(MTGCard mc1, MTGCard mc2) {

		if (getWeight(mc1) < getWeight(mc2))
			return -1;

		if (getWeight(mc1) == getWeight(mc2))
			return 0;

		return 1;
	}

	@Override
	public int getWeight(MTGCard mc) {
		switch(mc.getRarity())
		{

		case COMMON: return 1;
		case UNCOMMON:return 2;
		case RARE: return 3;
		case MYTHIC: return 4;
		case SPECIAL: return 5;
		case BONUS: return 6;
		default : return 1;
		}
		
	}

}

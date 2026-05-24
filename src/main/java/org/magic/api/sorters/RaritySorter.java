package org.magic.api.sorters;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.extra.MTGComparator;

public class RaritySorter implements MTGComparator<MTGCard> {

	@Override
	public String toString() {
		return "Type Sorter";
	}

	@Override
	public int compare(MTGCard mc1, MTGCard mc2) {
		return Integer.compare(getWeight(mc1), getWeight(mc2));
	}

	@Override
	public int getWeight(MTGCard mc) {
		return switch (mc.getRarity()) {
			case COMMON -> 1;
			case UNCOMMON -> 2;
			case RARE -> 3;
			case MYTHIC -> 4;
			case SPECIAL -> 5;
			case BONUS -> 6;
			default -> 1;
		};

	}

}

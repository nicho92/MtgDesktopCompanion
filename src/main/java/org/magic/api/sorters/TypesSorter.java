package org.magic.api.sorters;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComparator;

public class TypesSorter implements MTGComparator<MagicCard> {

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
		if (mc.isCreature())
			return 1;
		if (mc.isEnchantment())
			return 2;
		if (mc.isInstant())
			return 3;
		if (mc.isRitual())
			return 4;
		if (mc.isLand())
			return 7;
		if (mc.isArtifact())
			return 6;
		if (mc.isPlaneswalker())
			return 5;

		return 8;
	}

}

package org.magic.api.sorters;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComparator;

public class ColorSorter implements MTGComparator<MagicCard> {


	@Override
	public String toString() {
		return "Color Sorter";
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

		if (mc.getColors().size() > 1)
			return 7;

		if (mc.getColors().isEmpty() && mc.isLand())
			return 8;

		if (mc.getColors().isEmpty())
			return 6;

		return mc.getColors().get(0).getPosition();

	}

}

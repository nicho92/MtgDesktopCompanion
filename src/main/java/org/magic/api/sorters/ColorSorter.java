package org.magic.api.sorters;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGComparator;

public class ColorSorter implements MTGComparator<MTGCard> {


	@Override
	public String toString() {
		return "Color Sorter";
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

		if (mc.getColors().size() > 1)
			return 7;

		if (mc.getColors().isEmpty() && mc.isLand())
			return 8;

		if (mc.getColors().isEmpty())
			return 6;

		return mc.getColors().get(0).getPosition();

	}

}

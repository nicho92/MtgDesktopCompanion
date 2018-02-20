package org.magic.sorters;

import java.util.Comparator;

import org.magic.api.beans.MagicCard;

public interface MTGComparator<MagicCard> extends Comparator<MagicCard> {
	
	public int getWeight(MagicCard mc);

}

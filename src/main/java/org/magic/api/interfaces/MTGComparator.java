package org.magic.api.interfaces;

import java.util.Comparator;

public interface MTGComparator<MagicCard> extends Comparator<MagicCard> {
	
	public int getWeight(MagicCard mc);

}

package org.magic.api.interfaces;

import java.util.Comparator;

import org.magic.api.beans.MagicCard;

public interface MTGComparator<T extends MagicCard> extends Comparator<MagicCard> {

	public int getWeight(T mc);


}

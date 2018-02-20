package org.magic.sorters;

import org.magic.api.beans.MagicCard;

public class CmcSorter implements MTGComparator<MagicCard> {

	@Override
	public int compare(MagicCard o1, MagicCard o2) {
		if(o1.getCmc()<o2.getCmc())
			return -1;
		
		if(o1.getCmc()>o2.getCmc())
			return 1;
					
		return 0;
	}

	@Override
	public int getWeight(MagicCard mc) {
		return mc.getCmc()+1;
	}

}

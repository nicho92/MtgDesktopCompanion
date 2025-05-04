package org.magic.api.sorters;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.interfaces.extra.MTGComparator;

public class CardsDeckSorter implements MTGComparator<MTGCard> {

	MTGDeck deck;


	@Override
	public String toString() {
		return "Deck Sorter";
	}

	@Override
	public int compare(MTGCard o1, MTGCard o2) {
		var ret =0;
		try
		{
			ret = test(o1, o2);
			if (ret == 0)
				ret = name(o1, o2);
		}
		catch(Exception _)
		{
			ret = 0;
		}

		return ret;
	}

	public CardsDeckSorter(MTGDeck d)
	{
		this.deck=d;
	}


	private int test(MTGCard o1, MTGCard o2) {

		if (getWeight(o1) < getWeight(o2))
			return -1;

		if (getWeight(o1) == getWeight(o2))
			return 0;

		return 1;

	}

	private int land(MTGCard mc) {
		if (mc.getName().equalsIgnoreCase("Plains"))
			return 6;

		if (mc.getName().equalsIgnoreCase("Island"))
			return 7;

		if (mc.getName().equalsIgnoreCase("Swamp"))
			return 8;

		if (mc.getName().equalsIgnoreCase("Mountain"))
			return 9;

		return 10;
	}

	private int name(MTGCard o1, MTGCard o2) {
		return o1.getName().compareTo(o2.getName());
	}

	@Override
	public int getWeight(MTGCard mc) {



		if(deck.getCommander()==mc)
			return 0;

		if(mc.isPlaneswalker())
			return 1;

		if(mc.isCreature() && !mc.isArtifact())
			return 2;

		if(mc.isArtifact())
			return 3;

		if(mc.isRitual())
			return 4;

		if(mc.isInstant())
			return 5;


		if(mc.isEnchantment())
			return 6;



		if(mc.isLand() && !mc.isBasicLand())
			return 7;

		if(mc.isBasicLand())
			return land(mc);


		return 100;
	}

}

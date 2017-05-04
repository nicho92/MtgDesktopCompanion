package org.magic.game.model.counters;

import org.magic.game.gui.components.DisplayableCard;

public class LoyaltyCounter extends AbstractCounter {

	int value;
	
	public LoyaltyCounter(int value) {
		this.value=value;
	}

	@Override
	public void apply(DisplayableCard displayableCard) {
		
		int loy = 0;
		try{
			loy = displayableCard.getMagicCard().getLoyalty();
		}catch(Exception e)
		{		}
		
		displayableCard.getMagicCard().setLoyalty(loy+value);
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		int loy = 0;
		try{
			loy = displayableCard.getMagicCard().getLoyalty();
		}catch(Exception e)
		{		}
		
		displayableCard.getMagicCard().setLoyalty(loy-value);
		
	}

	@Override
	public String describe() {
		if(value==1)
			return "put a loyalty counter";
		else
			return "put "+value+" loyalty counter";
	}
}

package org.magic.game.model.counters;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.services.MTGLogger;

public class LoyaltyCounter extends AbstractCounter {

	int value;
	String label;
	
	public LoyaltyCounter(int value,String label) {
		this.value=value;
		this.label=label;
	}

	@Override
	public void apply(DisplayableCard displayableCard) {
		
		int loy = 0;
		try{
			loy = displayableCard.getMagicCard().getLoyalty();
		}catch(Exception e)
		{	MTGLogger.printStackTrace(e);	}
		
		displayableCard.getMagicCard().setLoyalty(loy+value);
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		int loy = 0;
		try{
			loy = displayableCard.getMagicCard().getLoyalty();
		}catch(Exception e)
		{	MTGLogger.printStackTrace(e);	}
		
		displayableCard.getMagicCard().setLoyalty(loy-value);
		
	}

	@Override
	public String describe() {
		String plus="";
		
		if(value>0)
			plus="+";
		else if(value==0)
			plus=" ";
		
		return plus+value+": "+label;
	}
}

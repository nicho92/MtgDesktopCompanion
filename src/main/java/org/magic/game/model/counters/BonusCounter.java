package org.magic.game.model.counters;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.services.MTGLogger;

public class BonusCounter extends AbstractCounter {

	int powerModifier;
	int toughnessModifier;
	
	public BonusCounter(int powerModifier,int toughnessModifier)
	{
		this.powerModifier=powerModifier;
		this.toughnessModifier=toughnessModifier;
	}

	@Override
	public int hashCode() {
		return this.hashCode();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		 if (obj == null)
			    return false;

			  if (this.getClass() != obj.getClass())
			    return false;
		
		return this.hashCode() ==((BonusCounter)obj).hashCode();
	}
	
	@Override
	public void apply(DisplayableCard displayableCard) {
		int power=0;
		int toughness=0;
		
		try{
			power = Integer.parseInt(displayableCard.getMagicCard().getPower());
		}
		catch(Exception e)
		{	
			power=0;
		}
		
		
		try{
			toughness = Integer.parseInt(displayableCard.getMagicCard().getToughness());
		}
		catch(Exception e)
		{	
			toughness=0;
		}
		
		power = power + powerModifier;
		toughness = toughness + toughnessModifier;
		
		displayableCard.getMagicCard().setPower(String.valueOf(power));
		displayableCard.getMagicCard().setToughness(String.valueOf(toughness));
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		int power=0;
		int toughness=0;
		
		try{
			power = Integer.parseInt(displayableCard.getMagicCard().getPower());
		}
		catch(Exception e)
		{MTGLogger.printStackTrace(e);	}
		
		
		try{
			toughness = Integer.parseInt(displayableCard.getMagicCard().getToughness());
		}
		catch(Exception e)
		{MTGLogger.printStackTrace(e);	}
		
		power = power - powerModifier;
		toughness = toughness - toughnessModifier;
		
		displayableCard.getMagicCard().setPower(String.valueOf(power));
		displayableCard.getMagicCard().setToughness(String.valueOf(toughness));
		
	}

	@Override
	public String describe() {
		
		StringBuilder build = new StringBuilder();
		
		if(powerModifier>=0)
			build.append("+").append(powerModifier);
		else
			build.append(powerModifier);
		
		build.append("/");	
			
		if(toughnessModifier>=0)
			build.append("+").append(toughnessModifier);
		else
			build.append(toughnessModifier);
		
		build.append(" counter");
		return build.toString();

	}
	
	
	
}

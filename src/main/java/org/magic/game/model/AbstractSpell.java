package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.model.costs.Cost;
import org.magic.game.model.effects.AbstractEffect;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractSpell implements Spell  {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected MagicCard card;
	protected List<Cost> costs;
	protected List<AbstractEffect> effects;
	
	public boolean hasCost()
	{
		return !costs.isEmpty();
	}
	
	
	public AbstractSpell()
	{
		costs=new ArrayList<>();
		effects=new ArrayList<>();
	}
	
	public void addCost(Cost e) {
		costs.add(e);
	}
	
	public void setCost(Cost c) {
		costs.clear();
		addCost(c);
		
	}

	public void setCosts(List<Cost> costs) {
		this.costs = costs;
	}
	
	
	public List<AbstractEffect> getEffects() {
		return effects;
	}
	
	public void addEffect(AbstractEffect e) {
		effects.add(e);
	}
	
	public void setEffects(List<AbstractEffect> effects) {
		this.effects = effects;
	}
	
	public Cost getCost()
	{
		if(!costs.isEmpty())
			return getCosts().get(0);
		else
			return null;
	}
	
	public List<Cost> getCosts() {
		return costs;
	}
	
	
	public MagicCard getCard() {
		return card;
	}
	
	public void setCard(MagicCard card) {
		this.card = card;
	}

}

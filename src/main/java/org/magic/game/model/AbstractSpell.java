package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.game.model.costs.Cost;
import org.magic.game.model.effects.AbstractEffect;
import org.magic.services.logging.MTGLogger;

public abstract class AbstractSpell implements Spell  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	protected MagicCard card;
	protected transient List<Cost> costs;
	protected transient List<AbstractEffect> effects;
	protected boolean resolved;
	protected transient List<AbstractAbilities> abilities;
	protected AbstractSpell target;
	
	
	public void setTarget(AbstractSpell target) {
		this.target = target;
	}

	public AbstractSpell getTarget(){
		return target;
	}
	
	
	
	public boolean isResolved() {
		return resolved;
	}
	

	public String getTitle()
	{
		return getCard().getName();
	}

	@Override
	public void resolve() {
		resolved=true;
	}

	
	
	
	public boolean hasCost()
	{
		return !costs.isEmpty();
	}
	
	protected AbstractSpell()
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

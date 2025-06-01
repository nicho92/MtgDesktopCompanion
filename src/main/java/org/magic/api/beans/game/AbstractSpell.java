package org.magic.api.beans.game;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.game.model.costs.Cost;
import org.magic.game.model.effects.AbstractEffect;

public abstract class AbstractSpell implements Spell  {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected MTGCard card;
	protected transient List<Cost> costs;
	protected transient List<AbstractEffect> effects;
	protected boolean resolved;
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


	@Override
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

	@Override
	public List<Cost> getCosts() {
		return costs;
	}


	public MTGCard getCard() {
		return card;
	}

	public void setCard(MTGCard card) {
		this.card = card;
	}

}

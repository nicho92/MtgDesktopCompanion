package org.magic.game.model.abilities;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.abilities.costs.Cost;
import org.magic.game.model.abilities.costs.LoyaltyCost;
import org.magic.game.model.abilities.effects.Effect;

public abstract class AbstractAbilities {

	private MagicCard card;
	private List<Cost> costs;
	private List<Effect> effects;
	
	public AbstractAbilities() {
		effects = new ArrayList<>();
		costs=new ArrayList<>();
	}
	
	
	public List<Effect> getEffects() {
		return effects;
	}
	
	public void addEffect(Effect e) {
		effects.add(e);
	}
	
	public void addCost(Cost e) {
		costs.add(e);
	}
	
	public void setCost(Cost c) {
		costs.clear();
		addCost(c);
		
	}
	
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}
	
	public Cost getCost()
	{
		return costs.get(0);
	}
	
	public List<Cost> getCosts() {
		return costs;
	}
	
	public void setCosts(List<Cost> costs) {
		this.costs = costs;
	}
	
	public MagicCard getCard() {
		return card;
	}
	
	public void setCard(MagicCard card) {
		this.card = card;
	}
	
	public boolean isStatic()
	{
		return false;
	}
	
	public boolean isTriggered()
	{
		return false;
	}
	
	public boolean isMana()
	{
		return true;
	}	

	public boolean isLoyalty()
	{
		return false;
	}
	
	public boolean isStackable()
	{
		return true;
	}
	
}

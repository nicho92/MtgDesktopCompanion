package org.magic.game.model.abilities;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.abilities.costs.Cost;
import org.magic.game.model.abilities.effects.Effect;

public abstract class AbstractAbilities {

	private MagicCard card;
	private Cost cost;
	private List<Effect> effects;
	
	public AbstractAbilities() {
		effects = new ArrayList<>();
	}
	
	
	public List<Effect> getEffects() {
		return effects;
	}
	
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}
	
	public Cost getCost() {
		return cost;
	}
	
	public void setCost(Cost cost) {
		this.cost = cost;
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

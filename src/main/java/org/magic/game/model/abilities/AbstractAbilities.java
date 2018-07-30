package org.magic.game.model.abilities;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.abilities.costs.Cost;
import org.magic.game.model.abilities.costs.LoyaltyCost;
import org.magic.game.model.abilities.effects.Effect;
import org.utils.patterns.observer.Observer;

public abstract class AbstractAbilities extends AbstractSpell {

	
	private static final long serialVersionUID = 1L;
	private transient List<Effect> effects;
	
	public AbstractAbilities() {
		super();
		effects = new ArrayList<>();
	}
	
	
	public List<Effect> getEffects() {
		return effects;
	}
	
	public void addEffect(Effect e) {
		effects.add(e);
	}
	
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

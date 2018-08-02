package org.magic.game.model.abilities;

import org.magic.game.model.AbstractSpell;

public abstract class AbstractAbilities extends AbstractSpell {

	public AbstractAbilities() {
		super();
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
		return false;
	}	

	public boolean isLoyalty()
	{
		return false;
	}
	
	public boolean isActivated()
	{
		return false;
	}
	
	public boolean isStackable()
	{
		return true;
	}

	public String getTitle()
	{
		return card.getName();
	}
}

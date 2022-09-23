package org.magic.game.model.abilities;

import org.magic.game.model.AbstractSpell;

public abstract class AbstractAbilities extends AbstractSpell {

	private static final long serialVersionUID = 1L;

	protected AbstractAbilities() {
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

	@Override
	public boolean isStackable()
	{
		return true;
	}



}

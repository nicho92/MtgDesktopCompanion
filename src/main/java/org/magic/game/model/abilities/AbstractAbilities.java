package org.magic.game.model.abilities;

import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.game.model.AbstractSpell;

public abstract class AbstractAbilities extends AbstractSpell {

	private static final long serialVersionUID = 1L;

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
	
	

}

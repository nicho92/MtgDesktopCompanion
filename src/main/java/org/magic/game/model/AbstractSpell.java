package org.magic.game.model;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public abstract class AbstractSpell extends AbstractAction{

	protected transient Logger logger = MTGLogger.getLogger(this.getClass());

	public abstract String getCost();
	public abstract boolean isStackable(); 
	
	public String getDescription()
	{
		return getValue(SHORT_DESCRIPTION).toString();
	}
	
	public String getName()
	{
		return getValue(NAME).toString();
	}
	
	public AbstractSpell(String name, String description,KeyEvent k) {
		super(name);
		putValue(SHORT_DESCRIPTION,description);
		putValue(MNEMONIC_KEY, k);
	}
	
	
	public AbstractSpell(String name, String description) {
		super(name);
		putValue(SHORT_DESCRIPTION,description);
	}
}

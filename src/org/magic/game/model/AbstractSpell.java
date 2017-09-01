package org.magic.game.model;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

public abstract class AbstractSpell extends AbstractAction{

	@Override
	public abstract void actionPerformed(ActionEvent paramActionEvent);
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
	/*
	public abstract void resolve();
	
	public abstract void unresolve();
	*/

}

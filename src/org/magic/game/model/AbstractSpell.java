package org.magic.game.model;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;

public abstract class AbstractSpell extends AbstractAction{

	@Override
	public abstract void actionPerformed(ActionEvent paramActionEvent);
	
	public abstract String getCost();
	public abstract boolean isStackable(); 

	protected DisplayableCard card;
	
	public String getDescription()
	{
		return getValue(SHORT_DESCRIPTION).toString();
	}
	
	public AbstractSpell(String name, String description,DisplayableCard card,KeyEvent k) {
		super(name);
		putValue(SHORT_DESCRIPTION,description);
		putValue(MNEMONIC_KEY, k);
		this.card=card;
	}
	
	
	public AbstractSpell(String name, String description,DisplayableCard card) {
		super(name);
		putValue(SHORT_DESCRIPTION,description);
	}
	

}

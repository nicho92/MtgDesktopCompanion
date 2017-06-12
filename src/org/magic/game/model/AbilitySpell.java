package org.magic.game.model;

import java.awt.event.ActionEvent;

import org.magic.game.gui.components.DisplayableCard;

public abstract class AbilitySpell extends AbstractSpell{

	
	protected DisplayableCard card;

	public AbilitySpell(String name, String description,DisplayableCard card) {
		super(name, description);
		this.card=card;
	}
	
	@Override
	public void actionPerformed(ActionEvent paramActionEvent) {
		GameManager.getInstance().getStack().put(this);
		
	}
	
	@Override
	public String toString() {
		return getName() +" :" + getDescription();
	}
	

	@Override
	public abstract String getCost();

	public abstract boolean isStackable() ;

}

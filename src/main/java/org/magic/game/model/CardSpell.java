package org.magic.game.model;

import java.awt.event.ActionEvent;

import org.magic.game.gui.components.DisplayableCard;

public class CardSpell extends AbstractSpell{

	DisplayableCard c;
	
	public CardSpell(String name, String description,DisplayableCard card) {
		super(name,description);
		this.c=card;
	}
	
	@Override
	public void actionPerformed(ActionEvent paramActionEvent) {
		//do nothing
		
	}
	
	public DisplayableCard getDisplayableCard(){
		return c;
	}
	
	

	@Override
	public String getCost() {
		return c.getMagicCard().getCost();
	}

	@Override
	public boolean isStackable() {
		return !c.getMagicCard().getTypes().toString().toLowerCase().contains("land");
	}

	@Override
	public String toString() {
		return c.getMagicCard().toString();
	}
	
	
}

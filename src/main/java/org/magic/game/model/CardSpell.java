package org.magic.game.model;

import java.awt.event.ActionEvent;
import java.util.List;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.abilities.effects.Effect;
import org.magic.game.model.factories.CostsFactory;

public class CardSpell extends AbstractSpell {

	private DisplayableCard c;

	public CardSpell(DisplayableCard card) {
		super();
		this.c = card;
		setCost(CostsFactory.getInstance().parseCosts(c.getMagicCard().getCost()));
	}


	public DisplayableCard getDisplayableCard() {
		return c;
	}
	
	@Override
	public boolean isStackable() {
		return !c.getMagicCard().isLand();
	}

	@Override
	public String toString() {
		return c.getMagicCard().toString();
	}
	
	@Override
	public void resolve() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Effect> getEffects() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}

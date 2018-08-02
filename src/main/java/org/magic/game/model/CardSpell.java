package org.magic.game.model;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.effects.OneShotEffect;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.game.model.factories.CostsFactory;

public class CardSpell extends AbstractSpell {

	private DisplayableCard c;
	
	
	
	public CardSpell(DisplayableCard card) {
		super();
		this.c = card;
		setCard(c.getMagicCard());
		setCost(CostsFactory.getInstance().parseCosts(c.getMagicCard().getCost()));
		OneShotEffect e = new OneShotEffect();
					  e.setCard(card.getMagicCard());
					  e.setEffectDescription(card.getMagicCard().getText());
					  e.setCost(CostsFactory.getInstance().parseCosts(card.getMagicCard().getCost()));
		addEffect(e);
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
	


}

package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.LoyaltyCounter;

public class LoyaltyActions extends AbstractAction {

	
	private DisplayableCard card;
	private LoyaltyCounter val;

	public LoyaltyActions(DisplayableCard card,LoyaltyCounter loyaltyCounter) {
		putValue(NAME,loyaltyCounter.describe());
		putValue(SHORT_DESCRIPTION,loyaltyCounter.describe());
	
		this.card = card;
	    this.val=loyaltyCounter;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		card.addCounter(val);
	//	card.initActions();
		card.showLoyalty(true);
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() +" loyalty to " +  card.getMagicCard().getLoyalty());
	}

}

package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.LoyaltyCounter;

public class LoyaltyActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient LoyaltyCounter val;

	public LoyaltyActions(DisplayableCard card, LoyaltyCounter loyaltyCounter) {
		super(card,loyaltyCounter.describe());
		putValue(SHORT_DESCRIPTION, loyaltyCounter.describe());
		this.val = loyaltyCounter;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		card.addCounter(val);
		card.showLoyalty(true);
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() + " loyalty to " + card.getMagicCard().getLoyalty());
	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;
import org.magic.game.model.counters.BonusCounter;

public class BonusCounterActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient BonusCounter bonusCounter;

	public BonusCounterActions(DisplayableCard displayableCard, BonusCounter bonusCounter) {
		super(displayableCard);
		this.bonusCounter = bonusCounter;
		putValue(NAME, "put a " + bonusCounter.describe());
		putValue(SHORT_DESCRIPTION, "put a " + bonusCounter.describe());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		card.addCounter(bonusCounter);
		card.showPT(true);
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() + " P/T to "
				+ card.getMagicCard().getPower() + "/" + card.getMagicCard().getToughness());

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

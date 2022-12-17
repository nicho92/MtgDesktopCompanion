package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;

public class TransformActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public TransformActions(DisplayableCard card) {
		super(card,"Transform");
		putValue(SHORT_DESCRIPTION, "Transform the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().logAction("Transform " + card.getMagicCard());

		try {

			card.removeAllCounters();

			var rCard = card.getMagicCard().getRotatedCard();
			rCard.setRulings(card.getMagicCard().getRulings());
			card.setMagicCard(rCard);
			card.revalidate();
			card.repaint();
			card.initActions();

		} catch (Exception ex) {
			logger.error("error transformation", ex);
		}

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

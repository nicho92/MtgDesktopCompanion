package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.logging.MTGLogger;

public class TransformActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public TransformActions(DisplayableCard card) {
		super("Transform");
		putValue(SHORT_DESCRIPTION, "Transform the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_A);
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().logAction("Transform " + card.getMagicCard());

		try {

			card.removeAllCounters();

			MagicCard mc = card.getMagicCard().getRotatedCard();
			mc.setRulings(card.getMagicCard().getRulings());
			card.setMagicCard(mc);
			card.revalidate();
			card.repaint();
			card.initActions();

		} catch (Exception ex) {
			logger.error("error transformation", ex);
		}

	}

}

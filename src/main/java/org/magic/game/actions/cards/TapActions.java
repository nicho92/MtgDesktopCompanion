package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;

public class TapActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;

	public TapActions(DisplayableCard card) {
		super("Tap");
		putValue(SHORT_DESCRIPTION, "tap/untap the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (card.isTappable())
			card.tap(!card.isTapped());

	}

}

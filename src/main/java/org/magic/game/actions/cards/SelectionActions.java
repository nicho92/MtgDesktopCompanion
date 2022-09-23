package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;

public class SelectionActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;

	public SelectionActions(DisplayableCard card) {
		super("Select");
		putValue(SHORT_DESCRIPTION, "select the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		card.setSelected(!card.isSelected());
		card.repaint();
	}

}

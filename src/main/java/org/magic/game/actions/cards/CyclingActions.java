package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class CyclingActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;

	public CyclingActions(DisplayableCard card) {
		super("Cycling");
		putValue(SHORT_DESCRIPTION, "Cycling");
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		GamePanelGUI.getInstance().getPlayer().logAction("Cycling " + card);
		GamePanelGUI.getInstance().getPlayer().discardCardFromHand(card.getMagicCard());
		GamePanelGUI.getInstance().getHandPanel().remove(card);

		GamePanelGUI.getInstance().getPanelGrave().addComponent(card);
		GamePanelGUI.getInstance().getPanelGrave().postTreatment(card);

	}

}

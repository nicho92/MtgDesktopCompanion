package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class SelectedTapActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SelectedTapActions() {
		super("(Un)Tap Selected Cards");
		putValue(SHORT_DESCRIPTION, "Tap/Untap the cards on battlefields");
		putValue(MNEMONIC_KEY, KeyEvent.VK_U);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (DisplayableCard card : GamePanelGUI.getInstance().getPanelBattleField().getSelectedCards()) {
			if (card.isTappable())
				card.tap(!card.isTapped());
		}

	}

}

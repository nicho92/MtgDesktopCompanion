package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class UnselectAllAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public UnselectAllAction() {
		super("(Un)Select cards");
		putValue(SHORT_DESCRIPTION, "(un)select the cards on battlefields");
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (DisplayableCard card : GamePanelGUI.getInstance().getPanelBattleField().getSelectedCards()) {
			card.setSelected(!card.isSelected());
			card.repaint();
		}

	}

}

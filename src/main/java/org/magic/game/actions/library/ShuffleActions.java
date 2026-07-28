package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.GamePanelGUI;

public class ShuffleActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ShuffleActions() {
		putValue(NAME, "Shuffle library");
		putValue(SHORT_DESCRIPTION, "Shuffle the Library");
		putValue(MNEMONIC_KEY, KeyEvent.VK_F);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().shuffleLibrary();

	}

}

package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;

public class AttachActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	DisplayableCard mc;

	public AttachActions(DisplayableCard mc) {
		this.mc = mc;
		putValue(NAME, "Attach to");
		putValue(SHORT_DESCRIPTION, "Attach " + mc + " to another card on battlefield");
		putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// do nothing

	}

}

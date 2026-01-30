package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.GamePanelGUI;



public class FlipaCoinActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private SecureRandom r;

	public FlipaCoinActions() {
		super("Flip a Coin");
		putValue(SHORT_DESCRIPTION, "Flip a Coin");
		putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		r = new SecureRandom();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		var b = r.nextBoolean();
		if (b)
			GamePanelGUI.getInstance().getPlayer().logAction("Flip a coin : Tails");
		else
			GamePanelGUI.getInstance().getPlayer().logAction("Flip a coin : Heads");

	}

}

package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.tools.CryptoUtils;



public class RollAD20Actions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public RollAD20Actions() {
		super("Roll a D20");
		putValue(SHORT_DESCRIPTION, "Roll a D20");
		putValue(MNEMONIC_KEY, KeyEvent.VK_R);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		int b = CryptoUtils.randomInt(20);
		
		GamePanelGUI.getInstance().getPlayer().logAction("Run a D20 : "+b);

	}

}

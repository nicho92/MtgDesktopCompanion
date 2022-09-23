package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.apache.commons.lang3.RandomUtils;
import org.magic.game.gui.components.GamePanelGUI;



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
		var b = RandomUtils.nextInt(1,21);

		GamePanelGUI.getInstance().getPlayer().logAction("Run a D20 : "+b);

	}

}

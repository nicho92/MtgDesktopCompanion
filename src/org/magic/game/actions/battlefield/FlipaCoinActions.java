package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.GamePanelGUI;

public class FlipaCoinActions extends AbstractAction {

	
	public FlipaCoinActions() {
		super("Flip a Coin");
		putValue(SHORT_DESCRIPTION,"Flip a Coin");
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
			GamePanelGUI.getInstance().getPlayer().flipCoin();
	}

}

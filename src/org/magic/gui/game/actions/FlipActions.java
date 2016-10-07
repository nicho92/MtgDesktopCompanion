package org.magic.gui.game.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class FlipActions extends AbstractAction {

	
	private DisplayableCard card;

	public FlipActions(String text, String desc,Integer mnemonic, DisplayableCard card) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
	        putValue(MNEMONIC_KEY, mnemonic);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		card.flip(true);
		GamePanelGUI.getInstance().getPlayer().logAction("Flip " + card.getMagicCard());
	}

}

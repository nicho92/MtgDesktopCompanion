package org.magic.gui.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class FlipActions extends AbstractAction {

	
	private DisplayableCard card;

	public FlipActions(DisplayableCard card) {
			super("Flip");
			putValue(SHORT_DESCRIPTION,"Flip the card");
	        putValue(MNEMONIC_KEY,KeyEvent.VK_F);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		card.flip(true);
		GamePanelGUI.getInstance().getPlayer().logAction("Flip " + card.getMagicCard());
	}

}

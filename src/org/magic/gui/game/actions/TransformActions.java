package org.magic.gui.game.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class TransformActions extends AbstractAction {

	
	private DisplayableCard card;

	public TransformActions(DisplayableCard card) {
			super("Transform");
			putValue(SHORT_DESCRIPTION,"Transform the card");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().logAction("Transform " + card.getMagicCard());
		card.transform();
		card.initActions();
		
	}

}

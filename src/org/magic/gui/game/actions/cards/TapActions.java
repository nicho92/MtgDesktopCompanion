package org.magic.gui.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class TapActions extends AbstractAction {

	
	private DisplayableCard card;

	public TapActions(DisplayableCard card) {
			super("Tap");
			putValue(SHORT_DESCRIPTION,"tap the card");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(card.isTappable())
		{
				
				if(card.isTapped())
				{
					card.tap(false);
					GamePanelGUI.getInstance().getPlayer().logAction("Untap " + card.getMagicCard());
				}
				else
				{
					card.tap(true);
					GamePanelGUI.getInstance().getPlayer().logAction("Tap " + card.getMagicCard());
				}
		}

	}

}

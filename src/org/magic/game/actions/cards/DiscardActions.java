package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class DiscardActions extends AbstractAction{
	private DisplayableCard card;

	public DiscardActions(DisplayableCard card) {
			super("Discard");
			putValue(SHORT_DESCRIPTION,"Discard the card");
	        putValue(MNEMONIC_KEY,KeyEvent.VK_D);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		card.flip(true);
		switch(card.getOrigine())
		{
		case BATTLEFIELD:GamePanelGUI.getInstance().getPlayer().discardCardFromBattleField(card.getMagicCard());
		case HAND: GamePanelGUI.getInstance().getPlayer().discardCardFromHand(card.getMagicCard());
		}
		
	}
}

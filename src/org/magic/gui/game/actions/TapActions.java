package org.magic.gui.game.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.game.GameManager;
import org.magic.game.Player;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class TapActions extends AbstractAction {

	
	private DisplayableCard card;

	public TapActions(String text, String desc,Integer mnemonic, DisplayableCard card) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
	        putValue(MNEMONIC_KEY, mnemonic);
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

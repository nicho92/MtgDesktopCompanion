package org.magic.gui.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.components.BattleFieldPanel;
import org.magic.gui.game.components.DisplayableCard;
import org.magic.gui.game.components.GamePanelGUI;

public class UntapAllAction extends AbstractAction {

	BattleFieldPanel battleFieldPanel;
	
	public UntapAllAction(BattleFieldPanel battleFieldPanel) {
			super("Untap All Cards");
			putValue(SHORT_DESCRIPTION,"untap the cards on battlefields");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
	        this.battleFieldPanel=battleFieldPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for(DisplayableCard card : battleFieldPanel.getCards())
		{
			if(card.isTappable())
			{
				if(card.isTapped())
				{
					card.tap(false);
					GamePanelGUI.getInstance().getPlayer().logAction("Untap " + card.getMagicCard());
				}
			}
		}

	}

}

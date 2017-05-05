package org.magic.game.actions.battlefield;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.BattleFieldPanel;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class SelectedTapActions extends AbstractAction {

	BattleFieldPanel battleFieldPanel;
	
	public SelectedTapActions(BattleFieldPanel battleFieldPanel) {
			super("(Un)Tap Selected Cards");
			putValue(SHORT_DESCRIPTION,"Tap/Untap the cards on battlefields");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
	        this.battleFieldPanel=battleFieldPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for(DisplayableCard card : battleFieldPanel.getSelectedCards())
		{
			if(card.isTappable())
				card.tap(!card.isTapped());
		}

	}

}

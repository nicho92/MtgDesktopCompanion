package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.BonusCounter;

public class BonusCounterActions  extends AbstractAction{

	private DisplayableCard card;
	private int val;
	private BonusCounter bonusCounter;
	
	public BonusCounterActions(DisplayableCard displayableCard, BonusCounter bonusCounter) 
	{
			this.card = displayableCard;
			this.bonusCounter=bonusCounter;
			putValue(NAME,bonusCounter.describe());
			putValue(SHORT_DESCRIPTION,bonusCounter.describe());
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		card.addCounter(bonusCounter);
		card.showPT(true);
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() +" P/T to " + card.getMagicCard().getPower()+"/"+card.getMagicCard().getToughness());

	}

}

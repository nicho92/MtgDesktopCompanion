package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.AbstractCounter;
import org.magic.game.model.counters.BonusCounter;

public class RemoveCounterActions  extends AbstractAction{

	private DisplayableCard card;
	private AbstractCounter counter;
	
	public RemoveCounterActions(DisplayableCard displayableCard, AbstractCounter counter) 
	{
			this.card = displayableCard;
			this.counter=counter;
			putValue(NAME,"remove a " + counter.describe());
			putValue(SHORT_DESCRIPTION,"remove a " + counter.describe());
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		card.removeCounter(counter);
		card.initActions();
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("remove a " + counter);

	}

}

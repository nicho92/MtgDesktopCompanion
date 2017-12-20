package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.counters.ItemCounter;

public class ItemCounterActions  extends AbstractAction{

	private DisplayableCard card;
	private ItemCounter itemCounter;
	
	public ItemCounterActions(DisplayableCard displayableCard, ItemCounter itemCounter) 
	{
			this.card = displayableCard;
			this.itemCounter=itemCounter;
			putValue(NAME,"put a " + itemCounter.describe());
			putValue(SHORT_DESCRIPTION,"put a " + itemCounter.describe());
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String counter = JOptionPane.showInputDialog("Counter's name ?");
		itemCounter.setName(counter);
		
		card.addCounter(itemCounter);
		card.initActions();
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("add a " + itemCounter.describe() + " on " + card.getMagicCard().getName());

	}

}

package org.magic.gui.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class LoyaltyActions extends AbstractAction {

	
	private DisplayableCard card;
	private int val;

	public LoyaltyActions(DisplayableCard card,int val) {
		if(val>0)
		{
			putValue(NAME,"put a loyalty counter");
			putValue(SHORT_DESCRIPTION,"Add +"+ val +" loyalty");
		}
		else
		{
			putValue(NAME,"remove a loyalty counter");
			putValue(SHORT_DESCRIPTION,"remove "+ val +" loyalty");

		}
	    this.card = card;
	    this.val=val;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		card.getMagicCard().setLoyalty(card.getMagicCard().getLoyalty()+val);
		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() +" loyalty to " +  card.getMagicCard().getLoyalty());
	}

}

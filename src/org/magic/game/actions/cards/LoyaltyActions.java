package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

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
		int loy = card.getMagicCard().getLoyalty();
		card.getMagicCard().setLoyalty(loy+val);
		
		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() +" loyalty to " +  card.getMagicCard().getLoyalty());
	}

}

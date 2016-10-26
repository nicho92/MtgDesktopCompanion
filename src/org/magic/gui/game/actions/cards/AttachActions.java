package org.magic.gui.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.DisplayableCardsChooser;
import org.magic.gui.game.GamePanelGUI;

public class AttachActions extends AbstractAction {

	
	private DisplayableCard card;

	public AttachActions(DisplayableCard card) {
			super("Attach to");
			putValue(SHORT_DESCRIPTION, "Attach a card to another");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		List<DisplayableCard> cards = GamePanelGUI.getInstance().getPanelBattleField().getCards();
							  cards.remove(card);
							  
		DisplayableCardsChooser choose = new DisplayableCardsChooser(cards);
		choose.setVisible(true);
		
		DisplayableCard c = choose.getSelectedCard();
		
		if(c!=null)
		{	
			card.getAttachedCards().add(c);
			GamePanelGUI.getInstance().getPlayer().logAction("attach " + card.getMagicCard() + " to " + c.getMagicCard());
		}
	}

}

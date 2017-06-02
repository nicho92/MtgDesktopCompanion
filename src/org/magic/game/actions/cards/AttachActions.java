package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.dialog.DisplayableCardsChooser;

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

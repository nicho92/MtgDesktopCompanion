package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class TransformActions extends AbstractAction {

	
	private DisplayableCard card;

	
	
	public TransformActions(DisplayableCard card) {
			super("Transform");
			putValue(SHORT_DESCRIPTION,"Transform the card");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().logAction("Transform " + card.getMagicCard());
		
		try {
			
			card.removeAllCounters();
			
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", card.getMagicCard().getRotatedCardName(), card.getMagicCard().getEditions().get(0)).get(0);
			mc.setRulings(card.getMagicCard().getRulings());
			card.setMagicCard(mc);
			card.revalidate();
			card.repaint();
			card.initActions();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}

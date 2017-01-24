package org.magic.gui.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;

public class TokensActions extends AbstractAction {

			
			private DisplayableCard card;

			public TokensActions(DisplayableCard card) {
				super("Generate a token");
				putValue(SHORT_DESCRIPTION, "Generate a token");
		        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
		        this.card = card;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					MagicCard tok = GamePanelGUI.getInstance().getTokenGenerator().generateTokenFor(card.getMagicCard() );
					DisplayableCard dc = new DisplayableCard( tok, card.getWidth(), card.getHeight(),true);
					dc.setMagicCard(tok);
					GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
					GamePanelGUI.getInstance().getPanelBattleField().revalidate();
					GamePanelGUI.getInstance().getPanelBattleField().repaint();
					GamePanelGUI.getInstance().getPlayer().logAction("generate " + tok + " token");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				
			}
}

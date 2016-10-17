package org.magic.gui.game.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.MagicCard;
import org.magic.gui.MagicGUI;
import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;
import org.magic.services.CockatriceTokenProvider;

public class EmblemActions extends AbstractAction {

			
			private DisplayableCard card;

			public EmblemActions(DisplayableCard card) {
				super("Generate a emblem");
				putValue(SHORT_DESCRIPTION, "Generate a emblem");
		        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		        this.card = card;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					MagicCard tok = GamePanelGUI.getInstance().getTokenGenerator().generateEmblemFor(card.getMagicCard() );
					DisplayableCard dc = new DisplayableCard( tok, card.getWidth(), card.getHeight(),true);
					dc.setMagicCard(tok);
					GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
					GamePanelGUI.getInstance().getPanelBattleField().revalidate();
					GamePanelGUI.getInstance().getPanelBattleField().repaint();
					GamePanelGUI.getInstance().getPlayer().logAction("generate " + tok + " emblem");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				
			}
}

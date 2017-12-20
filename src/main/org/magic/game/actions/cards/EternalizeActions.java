package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class EternalizeActions extends AbstractAction {

			
			private DisplayableCard card;

			public EternalizeActions(DisplayableCard card) {
				super("Eternalize");
				putValue(SHORT_DESCRIPTION, "Eternalize a creature");
		        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		        this.card = card;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					MagicCard tok = GamePanelGUI.getInstance().getTokenGenerator().generateTokenFor(card.getMagicCard() );
					DisplayableCard dc = new DisplayableCard( tok, MTGControler.getInstance().getCardsDimension(),true);
					
					dc.setMagicCard(tok);
					GamePanelGUI.getInstance().getPlayer().exileCardFromGraveyard(card.getMagicCard());
					GamePanelGUI.getInstance().getPanelGrave().remove(card);
					GamePanelGUI.getInstance().getPanelBattleField().addComponent(dc);
					GamePanelGUI.getInstance().getPanelBattleField().revalidate();
					GamePanelGUI.getInstance().getPanelBattleField().repaint();
					GamePanelGUI.getInstance().getPanelGrave().postTreatment(card);
					GamePanelGUI.getInstance().getPlayer().playToken(tok);
					GamePanelGUI.getInstance().getPlayer().logAction("Embalm "+  card);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				
			}
}

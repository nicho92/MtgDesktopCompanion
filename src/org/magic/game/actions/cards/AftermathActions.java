package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class AftermathActions extends AbstractAction {

	
	private DisplayableCard card;
	private String cost;
	private String k = "Aftermath";
	
	
	public AftermathActions(DisplayableCard card) {
			super("Aftermath");
			putValue(SHORT_DESCRIPTION,"Aftermath " + card.getMagicCard().getRotatedCardName());
	        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	        this.card = card;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		MagicCard mc;
		try {
			mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", card.getMagicCard().getRotatedCardName(), card.getMagicCard().getEditions().get(0)).get(0);
			DisplayableCard card2 = new DisplayableCard(mc, MTGControler.getInstance().getCardsDimension(), true);
			GamePanelGUI.getInstance().getPlayer().logAction("Aftermath " + card2 +" for " + mc.getCost());
			
			
			GamePanelGUI.getInstance().getPlayer().getGraveyard().remove(card.getMagicCard());
			GamePanelGUI.getInstance().getPlayer().getGraveyard().add(mc);
			GamePanelGUI.getInstance().getPlayer().playCardFromGraveyard(mc);
			
			GamePanelGUI.getInstance().getPanelGrave().remove(card);
			GamePanelGUI.getInstance().getPanelGrave().postTreatment(card);
			
			GamePanelGUI.getInstance().getPanelBattleField().addComponent(card2);
			GamePanelGUI.getInstance().getPanelBattleField().updatePanel();
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
	}

}

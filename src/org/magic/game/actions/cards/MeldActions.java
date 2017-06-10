package org.magic.game.actions.cards;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicRuling;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.PositionEnum;
import org.magic.gui.MagicGUI;
import org.magic.services.MTGControler;

public class MeldActions extends AbstractAction {

	private DisplayableCard card;
	private String cost;
	private String meldWith="";
	
	public MeldActions(DisplayableCard card) {
			super("Meld into " + card.getMagicCard().getRotatedCardName());
			putValue(SHORT_DESCRIPTION,"Meld the cards with bigger one !");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_M);
	        this.card = card;
	    	parse(card.getMagicCard().getText());
	}
	
	public void parse(String test)
	{
		if(test.contains("(Melds with "))
		{
			meldWith=test.substring(test.indexOf("(Melds with ")+"(Melds with ".length(), test.indexOf(".)")).trim();
		}
		else if (test.contains("and a creature named"))
		{
			
			meldWith=test.substring(test.indexOf("a creature named ")+"a creature named ".length(), test.indexOf(", exile them")).trim();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
			DisplayableCard card2;
				try {
					card2 = GamePanelGUI.getInstance().getPanelBattleField().lookupCardBy("name", meldWith).get(0);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Could not meld the card, " + meldWith +" is not on the battlefield");
					return;
				}
				
				
				GamePanelGUI.getInstance().getPlayer().logAction("Meld " + card.getMagicCard() + " and " + meldWith +" to " + card.getMagicCard().getRotatedCardName());
				
				card.removeAllCounters();
				GamePanelGUI.getInstance().getPlayer().exileCardFromBattleField(card.getMagicCard());
				GamePanelGUI.getInstance().getPanelBattleField().remove(card);
				
				card2.removeAllCounters();
				GamePanelGUI.getInstance().getPlayer().exileCardFromBattleField(card2.getMagicCard());
				GamePanelGUI.getInstance().getPanelBattleField().remove(card2);
				
				MagicCard mc;
				try {
					mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", card.getMagicCard().getRotatedCardName(), card.getMagicCard().getEditions().get(0)).get(0);
					
					Dimension d = new Dimension((int)(MTGControler.getInstance().getCardsDimension().getWidth()*1.5),(int)(MTGControler.getInstance().getCardsDimension().getHeight()*1.5));
					DisplayableCard c = new DisplayableCard(mc,d, true);
					c.initActions();
					GamePanelGUI.getInstance().getPanelBattleField().addComponent(c);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			  
			GamePanelGUI.getInstance().getPanelBattleField().revalidate();
			GamePanelGUI.getInstance().getPanelBattleField().repaint();
				  
				  
		
	}

}

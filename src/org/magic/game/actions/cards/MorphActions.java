package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class MorphActions extends AbstractAction {

	private DisplayableCard card;

	public MorphActions(DisplayableCard card) {
			super("Morph");
			putValue(SHORT_DESCRIPTION,"Morph");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		if(!card.isRotated())
		{
			MagicCard mc = new MagicCard();
				  mc.setName("Morphed Creature");
				  mc.setPower("2");
				  mc.setToughness("2");
				  mc.getTypes().add("Creature");
				  mc.setCost("{3}");
				  mc.setEditions(card.getMagicCard().getEditions());
				  mc.setRotatedCardName(card.getMagicCard().getName());
				  mc.setRulings(card.getMagicCard().getRulings());
				  mc.setText("Morph");
				  mc.setLayout(card.getMagicCard().getLayout());
				  mc.setId(card.getMagicCard().getId());
				  card.setMagicCard(mc);
				  card.setRotated(true);
				  card.showPT(true);
				  card.initActions();
				  try {
					card.setImage(new ImageIcon(MTGControler.getInstance().getEnabledPicturesProvider().getBackPicture().getScaledInstance(card.getWidth(), card.getHeight(), BufferedImage.SCALE_SMOOTH)));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		else
		{
			
			try {
				MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", card.getMagicCard().getRotatedCardName(), card.getMagicCard().getEditions().get(0)).get(0);
				card.setMagicCard(mc);
				card.setRotated(false);
				card.removeAllCounters();
				card.showPT(false);
				card.initActions();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
				  
		  card.revalidate();
		  card.repaint();
				  
				  
		
	}

}

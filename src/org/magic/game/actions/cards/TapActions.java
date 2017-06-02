package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;

public class TapActions extends AbstractAction {

	
	private DisplayableCard card;

	public TapActions(DisplayableCard card) {
			super("Tap");
			putValue(SHORT_DESCRIPTION,"tap/untap the card");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(card.isTappable())
			card.tap(!card.isTapped());
		
		
		/*
		if(!card.isTappable())
			return;
	
		 if(card.isTapped())
		 {
			 GamePanelGUI.getInstance().getPlayer().logAction("Untap " + card.getMagicCard());
		 }
		 else
		 {
			if(GameManager.getInstance().getActualTurn().currentPhase()==PHASES.Attack)
				GamePanelGUI.getInstance().getPlayer().logAction("Attack with " + card.getMagicCard());
			else
				GamePanelGUI.getInstance().getPlayer().logAction("Tap " + card.getMagicCard());
		 }

		int angle=0;
		if(card.isTapped())
			angle=90;
		else
			angle=-90;
	
        int w = card.getWidth();
        int h = card.getHeight();
        int type = BufferedImage.TYPE_INT_RGB;  // other options, see api
        BufferedImage bfImage = new BufferedImage(h, w, type);
        Graphics2D g2 = bfImage.createGraphics();
        double x = (h - w)/2.0;
        double y = (w - h)/2.0;
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(Math.toRadians(angle), w/2.0, h/2.0);
        g2.drawImage(card.getImageIcon().getImage(), at,null);
        g2.dispose();
        card.setImage(new ImageIcon((Image)bfImage));
        card.setSize(h, w);
        
        if(card.isTapped())
        	card.setTapped(false);
        else
        	card.setTapped(true);
		
		*/
		
	}

}

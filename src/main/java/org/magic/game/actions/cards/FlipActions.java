package org.magic.game.actions.cards;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class FlipActions extends AbstractAction {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	private DisplayableCard card;

	public FlipActions(DisplayableCard card) {
			super("Flip");
			putValue(SHORT_DESCRIPTION,"Flip the card");
	        putValue(MNEMONIC_KEY,KeyEvent.VK_F);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", card.getMagicCard().getRotatedCardName(), card.getMagicCard().getEditions().get(0),true).get(0);
			card.setMagicCard(mc);
	        BufferedImage bufferedImage = new BufferedImage(card.getWidth(), card.getHeight(), BufferedImage.TYPE_INT_RGB);
	    		
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		    tx.translate(-bufferedImage.getWidth(null), -bufferedImage.getHeight(null));
		    AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		    bufferedImage = op.filter(bufferedImage, null);
			
	        Graphics2D g2 = bufferedImage.createGraphics();
			           g2.drawImage(card.getImageIcon().getImage(), tx,null);
			           g2.dispose();
			           card.setImage(new ImageIcon(bufferedImage));
	        
			           card.setRotated(true);
	        
			           card.revalidate();
			           card.repaint();
			           
			           card.initActions();
			           GamePanelGUI.getInstance().getPlayer().logAction("Flip " + card.getMagicCard());
		} catch (Exception ex) {
			logger.error(ex);
		}
		
	}
	
	@Override
	public String toString() {
		return "Flip " + card.getMagicCard();
	}

}

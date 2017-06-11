package org.magic.game.actions.cards;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn.PHASES;

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
		if (!card.isTappable())
			return;

		if (card.isTapped()) {
			GamePanelGUI.getInstance().getPlayer().logAction("Untap " + card);
		} else {
			if (GameManager.getInstance().getActualTurn().currentPhase() == PHASES.Attack)
				GamePanelGUI.getInstance().getPlayer().logAction("Attack with " + card);
			else
				GamePanelGUI.getInstance().getPlayer().logAction("Tap " + card);
		}

		int angle = 0;
		if (!card.isTapped())
			angle = 90;
		else
			angle = -90;

		int w = card.getWidth();
		int h = card.getHeight();
		int type = BufferedImage.TYPE_INT_RGB; // other options, see api
		BufferedImage bfImage = new BufferedImage(h, w, type);
		Graphics2D g2 = bfImage.createGraphics();
		double x = (h - w) / 2.0;
		double y = (w - h) / 2.0;
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
		g2.drawImage(card.getImage().getImage(), at, null);
		g2.dispose();
		card.setImage(new ImageIcon((Image) bfImage));
		card.setSize(h, w);
		card.setTapped(!card.isTapped());
		
		*/
		
	}

}

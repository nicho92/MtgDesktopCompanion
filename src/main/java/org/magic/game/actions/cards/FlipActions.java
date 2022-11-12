package org.magic.game.actions.cards;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;

public class FlipActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public FlipActions(DisplayableCard card) {
		super("Flip");
		putValue(SHORT_DESCRIPTION, "Flip the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		this.card = card;
	}
	
	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			MagicCard mc =card.getMagicCard().getRotatedCard();
			card.setMagicCard(mc);

			var bufferedImage = new BufferedImage(card.getWidth(), card.getHeight(),BufferedImage.TYPE_INT_RGB);
			var tx = AffineTransform.getScaleInstance(-1, -1);
							tx.translate(-bufferedImage.getWidth(null), -bufferedImage.getHeight(null));
							var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bufferedImage = op.filter(bufferedImage, null);

			Graphics2D g2 = bufferedImage.createGraphics();
			g2.drawImage(card.getImageIcon().getImage(), tx, null);
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

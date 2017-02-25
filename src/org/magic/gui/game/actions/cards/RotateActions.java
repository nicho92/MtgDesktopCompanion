package org.magic.gui.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.magic.gui.game.components.DisplayableCard;
import org.magic.gui.game.components.GamePanelGUI;
import org.magic.services.MTGDesktopCompanionControler;

public class RotateActions extends AbstractAction {

	
	private DisplayableCard card;

	public RotateActions(DisplayableCard card) {
			super("Rotate");
			putValue(SHORT_DESCRIPTION, "Return the card");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(!card.isRotated())
			{
				card.setImage(new ImageIcon(MTGDesktopCompanionControler.getInstance().getEnabledPicturesProvider().getBackPicture().getScaledInstance(card.getWidth(), card.getHeight(), BufferedImage.SCALE_SMOOTH)));
				GamePanelGUI.getInstance().getPlayer().logAction("Rotate " + card.getMagicCard());
				card.setRotated(true);
			}
			else
			{
				card.setImage(new ImageIcon(MTGDesktopCompanionControler.getInstance().getEnabledPicturesProvider().getPicture(card.getMagicCard(), null).getScaledInstance(card.getWidth(), card.getHeight(), BufferedImage.SCALE_SMOOTH)));
				card.setRotated(false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("rotate " + card.getMagicCard());

	}

}

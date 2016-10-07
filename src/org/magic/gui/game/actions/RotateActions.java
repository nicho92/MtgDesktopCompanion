package org.magic.gui.game.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.GamePanelGUI;
import org.magic.services.MagicFactory;

public class RotateActions extends AbstractAction {

	
	private DisplayableCard card;

	public RotateActions(String text, String desc,Integer mnemonic, DisplayableCard card) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
	        putValue(MNEMONIC_KEY, mnemonic);
	        this.card = card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			card.setImage(new ImageIcon(MagicFactory.getInstance().getEnabledPicturesProvider().getBackPicture().getScaledInstance(card.getWidth(), card.getHeight(), BufferedImage.SCALE_SMOOTH)));
			GamePanelGUI.getInstance().getPlayer().logAction("Rotate " + card.getMagicCard());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		card.revalidate();
		card.repaint();
		GamePanelGUI.getInstance().getPlayer().logAction("rotate " + card.getMagicCard());

	}

}

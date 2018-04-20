package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class DrawActions extends AbstractAction {

	public DrawActions() {
		super("Draw a card");
		putValue(SHORT_DESCRIPTION, "Draw a card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_D);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			GamePanelGUI.getInstance().getPlayer().drawCard(1);
			GamePanelGUI.getInstance().getLblHandCount()
					.setText(String.valueOf(GamePanelGUI.getInstance().getPlayer().getHand().size()));
			GamePanelGUI.getInstance().getLblHandCount()
					.setText(String.valueOf(GamePanelGUI.getInstance().getPlayer().getLibrary().size()));
			DisplayableCard c = new DisplayableCard(
					GamePanelGUI.getInstance().getPlayer().getHand()
							.get(GamePanelGUI.getInstance().getPlayer().getHand().size() - 1),
					MTGControler.getInstance().getCardsDimension(), true);
			c.enableDrag(true);
			GamePanelGUI.getInstance().getHandPanel().addComponent(c);

		} catch (IndexOutOfBoundsException ex) {
			JOptionPane.showMessageDialog(null, "Not enougth cards in library", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}

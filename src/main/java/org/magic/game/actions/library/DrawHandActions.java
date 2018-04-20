package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.GamePanelGUI;

public class DrawHandActions extends AbstractAction {

	public DrawHandActions() {
		super("Draw Hand");
		putValue(SHORT_DESCRIPTION, "Draw a new Hand");
		putValue(MNEMONIC_KEY, KeyEvent.VK_D);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			GamePanelGUI.getInstance().getPlayer().mixHandAndLibrary();
			GamePanelGUI.getInstance().getPlayer().drawCard(7);
			GamePanelGUI.getInstance().getLblHandCount()
					.setText(String.valueOf(GamePanelGUI.getInstance().getPlayer().getHand().size()));
			GamePanelGUI.getInstance().getLblHandCount()
					.setText(String.valueOf(GamePanelGUI.getInstance().getPlayer().getLibrary().size()));
		} catch (IndexOutOfBoundsException ex) {
			JOptionPane.showMessageDialog(null, "Not enougth cards in library", "Error", JOptionPane.ERROR_MESSAGE);
		}
		GamePanelGUI.getInstance().getHandPanel().initThumbnails(GamePanelGUI.getInstance().getPlayer().getHand(), true,
				true);

	}

}

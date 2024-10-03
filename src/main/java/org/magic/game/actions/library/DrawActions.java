package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class DrawActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DrawActions() {
		super("Draw a card");
		putValue(SHORT_DESCRIPTION, "Draw a card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_D);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			GamePanelGUI.getInstance().getPlayer().drawCard(1);
			GamePanelGUI.getInstance().getLblHandCount().setText(String.valueOf(GamePanelGUI.getInstance().getPlayer().getHand().size()));
			GamePanelGUI.getInstance().getLblLibraryCount().setText(String.valueOf(GamePanelGUI.getInstance().getPlayer().getLibrary().size()));
			var c = new DisplayableCard(GamePanelGUI.getInstance().getPlayer().getHand().getCards().get(GamePanelGUI.getInstance().getPlayer().getHand().size() - 1),MTGControler.getInstance().getCardsGameDimension(), true);
			c.enableDrag(true);
			GamePanelGUI.getInstance().getHandPanel().addComponent(c);

		} catch (IndexOutOfBoundsException ex) {
			MTG.notifyError("Not enougth cards in library");
		}
	}

}

package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class DrawHandActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

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
			MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),"Not enougth cards in library",MESSAGE_TYPE.ERROR));
		}
		GamePanelGUI.getInstance().getHandPanel().initThumbnails(GamePanelGUI.getInstance().getPlayer().getHand().getCards(), true,true);

	}

}

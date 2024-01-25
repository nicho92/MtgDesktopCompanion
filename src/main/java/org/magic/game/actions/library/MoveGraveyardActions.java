package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.api.beans.MTGCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGControler;

public class MoveGraveyardActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MoveGraveyardActions() {
		putValue(NAME, "Put X cards in graveyard");
		putValue(SHORT_DESCRIPTION, "");
		putValue(MNEMONIC_KEY, KeyEvent.VK_G);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String res = JOptionPane.showInputDialog("How many card to discard ?");
		if (res != null) {
			var c = Integer.parseInt(res);
			List<MTGCard> disc = GamePanelGUI.getInstance().getPlayer().discardCardFromLibrary(c);

			for (MTGCard mc : disc) {
				GamePanelGUI.getInstance().getPanelGrave()
						.addComponent(new DisplayableCard(mc, MTGControler.getInstance().getCardsGameDimension(), true));
			}
			GamePanelGUI.getInstance().getPanelGrave().postTreatment(null);
		}

	}
}
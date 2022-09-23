package org.magic.game.actions.player;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.dialog.SearchCardFrame;
import org.magic.game.model.ZoneEnum;

public class SearchActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ZoneEnum pos;

	public SearchActions(ZoneEnum pos) {
		putValue(NAME, "Search in " + pos);
		putValue(SHORT_DESCRIPTION, "");
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		this.pos = pos;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().logAction("search in " + pos);
		var f = new SearchCardFrame(GamePanelGUI.getInstance().getPlayer(), pos);
		f.setVisible(true);

	}

}

package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.dialog.SearchCardFrame;
import org.magic.game.model.PositionEnum;

public class SearchActions extends AbstractAction {


	public SearchActions() {
		putValue(NAME,"Search in library");
		putValue(SHORT_DESCRIPTION,"");
		 putValue(MNEMONIC_KEY, KeyEvent.VK_S);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().logAction("search in library");
		SearchCardFrame f = new SearchCardFrame(GamePanelGUI.getInstance().getPlayer(),PositionEnum.LIBRARY);
		f.setVisible(true);
		
	}
	
}

package org.magic.gui.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.gui.game.GamePanelGUI;
import org.magic.gui.game.SearchLibraryFrame;

public class ShuffleActions extends AbstractAction {


	public ShuffleActions() {
		putValue(NAME,"Shuffle library");
		putValue(SHORT_DESCRIPTION,"Shuffle the Library");
		 putValue(MNEMONIC_KEY, KeyEvent.VK_F);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GamePanelGUI.getInstance().getPlayer().shuffleLibrary();
		
	}
	
}

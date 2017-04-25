package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.SearchLibraryFrame;

public class MoveGraveyardActions extends AbstractAction {


	public MoveGraveyardActions() {
		putValue(NAME,"Put X cards in graveyard");
		putValue(SHORT_DESCRIPTION,"");
		putValue(MNEMONIC_KEY, KeyEvent.VK_G);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String res = JOptionPane.showInputDialog("How many card to discard ?");
		if(res!=null)
		{
			GamePanelGUI.getInstance().getPlayer().discardCardFromLibrary(Integer.parseInt(res));
			
		}
		
	}
}
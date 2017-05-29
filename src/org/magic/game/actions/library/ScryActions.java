package org.magic.game.actions.library;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.SearchCardFrame;
import org.magic.game.model.PositionEnum;

public class ScryActions extends AbstractAction {


	public ScryActions() {
		putValue(NAME,"Scry X cards");
		putValue(SHORT_DESCRIPTION,"");
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String res = JOptionPane.showInputDialog("How many scry cards ?");
		if(res!=null)
		{
			new SearchCardFrame(GamePanelGUI.getInstance().getPlayer(),GamePanelGUI.getInstance().getPlayer().scry(Integer.parseInt(res)),PositionEnum.LIBRARY).setVisible(true);
		}
		
	}
}
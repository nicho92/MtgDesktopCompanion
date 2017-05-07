package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class BlockPhase extends AbstractAction {

	
	public BlockPhase() {
		super("Block");
		putValue(SHORT_DESCRIPTION, "Block Phase");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		setEnabled(false);
	}

}

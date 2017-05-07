package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class EndPhase extends AbstractAction {

	
	public EndPhase() {
		super("End");
		putValue(SHORT_DESCRIPTION, "End phase");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		setEnabled(false);
		
	}

}

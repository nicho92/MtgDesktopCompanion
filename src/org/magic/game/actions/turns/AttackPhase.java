package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class AttackPhase extends AbstractAction {

	
	public AttackPhase() {
		super("Attack");
		putValue(SHORT_DESCRIPTION, "Attack Phase");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
		setEnabled(false);
		
	}

}

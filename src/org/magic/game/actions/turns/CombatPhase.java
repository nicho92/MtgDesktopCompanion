package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class CombatPhase extends AbstractAction {

	
	
	String detail="-Abilities that trigger at the beginning of the main phase go onto the stack."
			+ "-The active player gains priority.";

	
	public CombatPhase() {
		super("Combat");
		putValue(SHORT_DESCRIPTION, detail);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		
		setEnabled(false);
		
	}

}

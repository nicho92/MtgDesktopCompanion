package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class MainPhase extends AbstractAction {

	int step;
	
	String detail="-Abilities that trigger at the beginning of the main phase go onto the stack.\n"
				+ "-The active player gains priority.";
	
	
	public MainPhase(int step) {
		super("Main");
		this.step=step;
		putValue(SHORT_DESCRIPTION, detail);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		setEnabled(false);
		
	}

}

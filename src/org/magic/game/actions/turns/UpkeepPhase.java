package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class UpkeepPhase extends AbstractAction {

	
	String detail = "-At the beginning of the upkeep step, any abilities that trigger either during the untap step or at the beginning of upkeep go on the stack.\n"
			+ "-Then the active player gains priority the first time during his or her turn.\n"
			+ "-During this step, all upkeep costs are paid.";
	
			
	public UpkeepPhase() {
		super("Upkeep");
		putValue(SHORT_DESCRIPTION, detail);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		setEnabled(false);
		
	}

}

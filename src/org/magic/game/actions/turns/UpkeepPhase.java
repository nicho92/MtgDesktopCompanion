package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.game.model.Turn;

public class UpkeepPhase extends AbstractAction {

	
	String detail = "-At the beginning of the upkeep step, any abilities that trigger either during the untap step or at the beginning of upkeep go on the stack.\n"
			+ "-Then the active player gains priority the first time during his or her turn.\n"
			+ "-During this step, all upkeep costs are paid.";
	
			
	public UpkeepPhase(Player p) {
		super("Upkeep");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonTo((JButton)ae.getSource());
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.Upkeep);
		
		setEnabled(false);
		
	}

}

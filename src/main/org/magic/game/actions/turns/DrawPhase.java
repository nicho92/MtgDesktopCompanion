package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.actions.library.DrawActions;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class DrawPhase extends AbstractAction {

	
	String detail="<html>-The active player draws a card from their library.<br/>"
			  +	"-Any abilities that trigger at the beginning of the draw step go on the stack.<br/>"
			  + "-The active player gains priority.</html>";


	
	public DrawPhase() {
		super("Draw");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
	
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.Draw);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton)ae.getSource());
		new DrawActions().actionPerformed(ae);
		setEnabled(false);
		
	}

}

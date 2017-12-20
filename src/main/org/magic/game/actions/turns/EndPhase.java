package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class EndPhase extends AbstractAction {

	String detail="<html>The end step, often referred to as the <b>end of turn</b> and previously known as the <b>end of turn</b>,<br/>"
			+ "is the first step of the ending phase. It is usually the last opportunity for a player to perform any action<br/>"
			+ "before the next player's turn starts.</html>";
	
	
	public EndPhase() {
		super("End");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.End);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton)ae.getSource());
		setEnabled(false);
		
	}

}

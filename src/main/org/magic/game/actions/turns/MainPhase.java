package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class MainPhase extends AbstractAction {

	int step;
	
	String detail="<html>Abilities that trigger at the beginning of the main phase go onto the stack.<br/>"
				+ "The active player gains priority.";
	
	
	public MainPhase(int step) {
		super("Main");
		this.step=step;
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.Main);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton)ae.getSource());
		setEnabled(false);
		
	}

}

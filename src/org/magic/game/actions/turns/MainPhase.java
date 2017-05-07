package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.Player;

public class MainPhase extends AbstractAction {

	int step;
	
	String detail="<html>Abilities that trigger at the beginning of the main phase go onto the stack.<br/>"
				+ "The active player gains priority.";
	
	
	public MainPhase(int step, Player p) {
		super("Main");
		this.step=step;
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonTo((JButton)ae.getSource());
		setEnabled(false);
		
	}

}

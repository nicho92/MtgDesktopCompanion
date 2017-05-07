package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.Player;

public class DrawPhase extends AbstractAction {

	
	String detail="<html>-The active player draws a card from their library.<br/>"
			  +	"-Any abilities that trigger at the beginning of the draw step go on the stack.<br/>"
			  + "-The active player gains priority.</html>";


	
	public DrawPhase(Player p) {
		super("Draw");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
	
		GamePanelGUI.getInstance().drawAction();
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonTo((JButton)ae.getSource());
		
		setEnabled(false);
		
	}

}

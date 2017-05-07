package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.Player;

public class EndCombatPhase extends AbstractAction {

	
	public EndCombatPhase(Player p) {
		super("End Combat");
		putValue(SHORT_DESCRIPTION, "");
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonTo((JButton)ae.getSource());
		setEnabled(false);
		
	}

}

package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class AttackPhase extends AbstractAction {

	
	public AttackPhase() {
		super("Attack");
		putValue(SHORT_DESCRIPTION, "Attack Phase");
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.Attack);

		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton)ae.getSource());
		setEnabled(false);
		
	}

}

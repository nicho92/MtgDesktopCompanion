package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.Turn;
import org.magic.game.gui.components.GamePanelGUI;

public class CombatPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String detail = "<html>-The active player gains priority.<br/>"
			+ "-Creatures assigned in this step are attacking.</html>";

	public CombatPhase() {
		super("Combat");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.COMBAT);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton) ae.getSource());
		setEnabled(false);

	}

}

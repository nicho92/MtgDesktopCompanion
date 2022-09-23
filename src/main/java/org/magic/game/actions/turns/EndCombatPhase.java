package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class EndCombatPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public EndCombatPhase() {
		super("End Combat");
		putValue(SHORT_DESCRIPTION, "");
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.END_COMBAT);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton) ae.getSource());

		setEnabled(false);

	}

}

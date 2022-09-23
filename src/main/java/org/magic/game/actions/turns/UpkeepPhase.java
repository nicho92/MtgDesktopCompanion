package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class UpkeepPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String detail = "<html>-At the beginning of the upkeep step, any abilities that trigger either during the untap step or at the beginning of upkeep go on the stack.<br/>"
			+ "-Then the active player gains priority the first time during his or her turn.<br/>"
			+ "-During this step, all upkeep costs are paid.</html>";

	public UpkeepPhase() {
		super("Upkeep");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton) ae.getSource());
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.UPKEEP);

		setEnabled(false);

	}

}

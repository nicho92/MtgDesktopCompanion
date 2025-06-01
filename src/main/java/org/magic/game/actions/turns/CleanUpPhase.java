package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.Turn;
import org.magic.game.gui.components.GamePanelGUI;

public class CleanUpPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String detail = "<html>The cleanup step is the second and final step of the ending phase. <br/>"
			+ "Spells and abilities may be played during this step only if the conditions for any state-based actions exist or if any abilities have triggered.<br/>"
			+ "In that case, those state-based actions are performed and/or those abilities go on the stack and the active player gets priority and players may cast spells and activate abilities.<br/>"
			+ " Once all players pass priority when the stack is empty, the step repeats.</html>";

	public CleanUpPhase() {
		super("Clean Up");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		GamePanelGUI.getInstance().getManaPoolPanel().clean();

		GameManager.getInstance().getStack().clean();

		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.CLEANUP);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton) ae.getSource());
		setEnabled(false);

	}

}

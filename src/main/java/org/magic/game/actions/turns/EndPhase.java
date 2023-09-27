package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.Turn;
import org.magic.game.gui.components.GamePanelGUI;

public class EndPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String detail = "<html>The end step, often referred to as the <b>end of turn</b> and previously known as the <b>end of turn</b>,<br/>"
			+ "is the first step of the ending phase. It is usually the last opportunity for a player to perform any action<br/>"
			+ "before the next player's turn starts.</html>";

	public EndPhase() {
		super("End");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.END);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton) ae.getSource());
		setEnabled(false);

	}

}

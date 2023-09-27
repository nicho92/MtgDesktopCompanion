package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.Player;
import org.magic.game.gui.components.GamePanelGUI;

public class EndTurnPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	Player p;

	public EndTurnPhase() {
		super("Init new turn");
		putValue(SHORT_DESCRIPTION, "Init new turn");
		setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		GameManager.getInstance().endTurn(GamePanelGUI.getInstance().getPlayer());
		GamePanelGUI.getInstance().getTurnsPanel().initTurn();
	}

}

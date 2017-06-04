package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.actions.library.DrawActions;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.game.model.Turn;

public class EndTurnPhase extends AbstractAction {

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

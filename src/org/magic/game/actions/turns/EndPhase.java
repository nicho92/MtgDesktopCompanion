package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class EndPhase extends AbstractAction {

	String detail="<html>The end step, often referred to as the <b>end of turn</b> and previously known as the <b>end of turn</b>,<br/>"
			+ "is the first step of the ending phase. It is usually the last opportunity for a player to perform any action<br/>"
			+ "before the next player's turn starts.</html>";
	
	
	public EndPhase() {
		super("End");
		putValue(SHORT_DESCRIPTION, detail);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		setEnabled(false);
		
	}

}

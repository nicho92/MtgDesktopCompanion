package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Turn;

public class CleanUpPhase extends AbstractAction {

	String detail = "<html>The cleanup step is the second and final step of the ending phase. <br/>"
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
		
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.Cleanup);
		GamePanelGUI.getInstance().getTurnsPanel().disableButtonsTo((JButton)ae.getSource());
		setEnabled(false);
		
	}

}

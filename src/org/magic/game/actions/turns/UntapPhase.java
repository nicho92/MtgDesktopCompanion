package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.game.model.Turn;

public class UntapPhase extends AbstractAction {

	
	String detail = "<html>-All permanents with phasing controlled by the active player phase out\n, and all phased-out permanents that were controlled by the active player simultaneously phase in.<br/>"
			+ "-The active player determines which permanents controlled by that player untap, then untaps all those permanents simultaneously.<br/>(The player will untap all permanents he or she controls unless a card effect prevents this.)<html>";	
	
	Player p;
	
	public UntapPhase(Player p) {
		super("Untap");
		putValue(SHORT_DESCRIPTION, detail);
		this.p=p;
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.Untap);
		
		for(DisplayableCard c : GamePanelGUI.getInstance().getPanelBattleField().getCards())
			if(c.isTapped())
				c.tap(false);
		
		
		
		setEnabled(false);
		
	}

}

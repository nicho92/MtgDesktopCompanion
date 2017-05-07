package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class UntapPhase extends AbstractAction {

	
	String detail = "<html>-All permanents with phasing controlled by the active player phase out\n, and all phased-out permanents that were controlled by the active player simultaneously phase in.<br/>"
			+ "-The active player determines which permanents controlled by that player untap, then untaps all those permanents simultaneously.<br/>(The player will untap all permanents he or she controls unless a card effect prevents this.)<html>";	
	
	
	public UntapPhase() {
		super("Untap");
		putValue(SHORT_DESCRIPTION, detail);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		for(DisplayableCard c : GamePanelGUI.getInstance().getPanelBattleField().getCards())
			if(c.isTapped())
				c.tap(false);
		
		setEnabled(false);
		
	}

}

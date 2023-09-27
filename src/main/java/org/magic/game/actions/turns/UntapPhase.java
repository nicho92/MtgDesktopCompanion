package org.magic.game.actions.turns;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.game.Turn;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class UntapPhase extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	String detail = "<html>-All permanents with phasing controlled by the active player phase out\n, and all phased-out permanents that were controlled by the active player simultaneously phase in.<br/>"
			+ "-The active player determines which permanents controlled by that player untap, then untaps all those permanents simultaneously.<br/>(The player will untap all permanents he or she controls unless a card effect prevents this.)<html>";

	Player p;

	public UntapPhase() {
		super("Untap");
		putValue(SHORT_DESCRIPTION, detail);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		GameManager.getInstance().getActualTurn().setCurrentPhase(Turn.PHASES.UNTAP);

		for (DisplayableCard c : GamePanelGUI.getInstance().getPanelBattleField().getCards())
			if (c.isTapped())
				c.tap(false);

		setEnabled(false);

	}

}

package org.magic.game.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.magic.api.beans.game.GameManager;
import org.magic.game.actions.turns.AttackPhase;
import org.magic.game.actions.turns.BlockPhase;
import org.magic.game.actions.turns.CleanUpPhase;
import org.magic.game.actions.turns.CombatPhase;
import org.magic.game.actions.turns.DamagePhase;
import org.magic.game.actions.turns.DrawPhase;
import org.magic.game.actions.turns.EndCombatPhase;
import org.magic.game.actions.turns.EndPhase;
import org.magic.game.actions.turns.EndTurnPhase;
import org.magic.game.actions.turns.MainPhase;
import org.magic.game.actions.turns.UntapPhase;
import org.magic.game.actions.turns.UpkeepPhase;

public class TurnsPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	JLabel lblTurnNumber;

	public TurnsPanel() {
		var flowLayout = (FlowLayout) getLayout();
		flowLayout.setVgap(1);
		flowLayout.setHgap(1);
		flowLayout.setAlignment(FlowLayout.LEFT);
		lblTurnNumber = new JLabel("Turn " + GameManager.getInstance().getTurns().size());
		add(lblTurnNumber);

		add(new JButton(new UntapPhase()));
		add(new JButton(new UpkeepPhase()));
		add(new JButton(new DrawPhase()));
		add(new JButton(new MainPhase(1)));
		add(new JButton(new CombatPhase()));
		add(new JButton(new AttackPhase()));
		add(new JButton(new BlockPhase()));
		add(new JButton(new DamagePhase()));
		add(new JButton(new EndCombatPhase()));
		add(new JButton(new MainPhase(2)));
		add(new JButton(new EndPhase()));
		add(new JButton(new CleanUpPhase()));
		add(new JButton(new EndTurnPhase()));
	}

	public void disableButtonsTo(JButton b) {
		for (Component c : getComponents()) {
			if (c instanceof JButton but) {
				if (!but.equals(b)) {
					but.getAction().setEnabled(false);
				} else {
					return;
				}
			}
		}
	}

	public void initTurn() {
		for (Component c : getComponents())
			if (c instanceof JButton b)
				b.getAction().setEnabled(true);

		lblTurnNumber.setText("Turn " + GameManager.getInstance().getTurns().size());
	}

}

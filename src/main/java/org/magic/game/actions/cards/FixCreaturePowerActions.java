package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class FixCreaturePowerActions extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private DisplayableCard card;

	public FixCreaturePowerActions(DisplayableCard displayableCard) {
		this.card = displayableCard;
		putValue(NAME, "Fix creature's power/toughness ");
		putValue(SHORT_DESCRIPTION, "Fix creature's power/toughness ");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String res = JOptionPane.showInputDialog("Power/Toughness ?");

		card.getMagicCard().setPower(res.split("/")[0]);
		card.getMagicCard().setToughness(res.split("/")[1]);
		card.showPT(true);
		card.revalidate();
		card.repaint();

		GamePanelGUI.getInstance().getPlayer().logAction("set " + card.getMagicCard().getName() + " P/T to "
				+ card.getMagicCard().getPower() + "/" + card.getMagicCard().getToughness());

	}

}

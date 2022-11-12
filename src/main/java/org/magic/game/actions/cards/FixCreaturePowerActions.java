package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;

public class FixCreaturePowerActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FixCreaturePowerActions(DisplayableCard displayableCard) {
		super(displayableCard,"Fix creature's power/toughness ");
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

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.model.ZoneEnum;

public class CyclingActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CyclingActions(DisplayableCard card) {
		super(card);
		putValue(SHORT_DESCRIPTION, "Cycling");
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		GamePanelGUI.getInstance().getPlayer().logAction("Cycling " + card);
		GamePanelGUI.getInstance().getPlayer().discardCardFromHand(card.getMagicCard());
		GamePanelGUI.getInstance().getHandPanel().remove(card);

		GamePanelGUI.getInstance().getPanelGrave().addComponent(card);
		GamePanelGUI.getInstance().getPanelGrave().postTreatment(card);

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.HAND;
	}

}

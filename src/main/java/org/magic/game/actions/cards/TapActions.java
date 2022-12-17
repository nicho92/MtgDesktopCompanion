package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.ZoneEnum;

public class TapActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public TapActions(DisplayableCard card) {
		super(card,"Tap");
		putValue(SHORT_DESCRIPTION, "tap/untap the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (card.isTappable())
			card.tap(!card.isTapped());

	}
	
	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}
	

}

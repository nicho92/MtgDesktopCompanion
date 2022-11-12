package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.ZoneEnum;

public class SelectionActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	public SelectionActions(DisplayableCard card) {
		super(card,"Select");
		putValue(SHORT_DESCRIPTION, "select the card");
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		card.setSelected(!card.isSelected());
		card.repaint();
	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.ZoneEnum;

public class AttachActions extends AbstractCardAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AttachActions(DisplayableCard mc) {
		super(mc, "Attach to");

		putValue(SHORT_DESCRIPTION, "Attach " + mc + " to another card on battlefield");
		putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// do nothing

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

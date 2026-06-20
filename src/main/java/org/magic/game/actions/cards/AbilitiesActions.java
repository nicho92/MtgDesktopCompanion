package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import org.magic.api.game.beans.GameManager;
import org.magic.api.game.beans.ZoneEnum;
import org.magic.game.actions.abbstract.AbstractCardAction;
import org.magic.game.interfaces.AbstractAbilities;

public class AbilitiesActions extends AbstractCardAction {

	private static final long serialVersionUID = 1L;
	private AbstractAbilities abs;

	public AbilitiesActions(AbstractAbilities abs) {
		super(null, abs.getCosts() + " " + abs.getEffects());
		this.abs = abs;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GameManager.getInstance().getStack().put(abs);

	}

	@Override
	public ZoneEnum playableFrom() {
		return ZoneEnum.BATTLEFIELD;
	}

}

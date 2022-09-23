package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.magic.game.model.GameManager;
import org.magic.game.model.abilities.AbstractAbilities;

public class AbilitiesActions extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private AbstractAbilities abs;


	public AbilitiesActions(AbstractAbilities abs) {
		super(abs.getCosts() +" " + abs.getEffects());
		this.abs=abs;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GameManager.getInstance().getStack().put(abs);

	}

}

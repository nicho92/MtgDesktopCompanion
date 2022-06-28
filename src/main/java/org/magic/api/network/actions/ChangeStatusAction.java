package org.magic.api.network.actions;

import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public class ChangeStatusAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public ChangeStatusAction(Player p) {
		super(p);
		setAct(ACTIONS.CHANGE_STATUS);
	}

	public ChangeStatusAction(Player p, STATUS s) {
		super(p);
		setAct(ACTIONS.CHANGE_STATUS);
		p.setState(s);
		
	}

	@Override
	public String toString() {
		return getInitiator() + " change his status to " + initiator.getState();
	}

}

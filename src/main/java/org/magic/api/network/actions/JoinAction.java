package org.magic.api.network.actions;

import org.magic.game.model.Player;

public class JoinAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JoinAction(Player p1) {
		super(p1);
		setAct(ACTIONS.JOIN);

	}

	@Override
	public String toString() {
		return getInitiator() + " join the channel";
		
	}
	
	
	
	
}

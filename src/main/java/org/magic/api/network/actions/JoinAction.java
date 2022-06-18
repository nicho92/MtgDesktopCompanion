package org.magic.api.network.actions;

import org.magic.game.model.Player;

public class JoinAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Player p1;

	public JoinAction(Player p1) {
		this.p1 = p1;
		setAct(ACTIONS.JOIN);

	}

	public Player getPlayer() {
		return p1;
	}

	@Override
	public String toString() {
		return getPlayer() + " join the channel";
	}

}

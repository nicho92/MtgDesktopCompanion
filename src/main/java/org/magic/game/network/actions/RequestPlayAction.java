package org.magic.game.network.actions;

import org.magic.game.model.Player;

public class RequestPlayAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Player p1;
	Player p2;

	public RequestPlayAction(Player p1, Player p2) {
		setAct(ACTIONS.REQUEST_PLAY);
		this.p1 = p1;
		this.p2 = p2;
	}

	public Player getRequestPlayer() {
		return p1;
	}

	public Player getAskedPlayer() {
		return p2;
	}

	@Override
	public String toString() {
		return getRequestPlayer() + " request " + getAskedPlayer() + " to play a game";
	}

}

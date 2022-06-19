package org.magic.api.network.actions;

import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public class ChangeStatusAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Player player;

	public ChangeStatusAction(Player p) {
		setAct(ACTIONS.CHANGE_STATUS);
		this.player = p;
	}

	public ChangeStatusAction(Player p, STATUS s) {
		setAct(ACTIONS.CHANGE_STATUS);
		p.setState(s);
		this.player = p;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return player + " change his status to " + player.getState();
	}

}

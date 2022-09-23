package org.magic.api.network.actions;

import java.io.Serializable;

import org.magic.game.model.Player;

public abstract class AbstractNetworkAction implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public enum ACTIONS {
		JOIN, SPEAK, LIST_PLAYER, CHANGE_STATUS, SEARCH
	}

	private ACTIONS act;
	protected Player initiator;

	public ACTIONS getAct() {
		return act;
	}

	public void setAct(ACTIONS act) {
		this.act = act;
	}


	protected AbstractNetworkAction(Player p) {
		this.initiator=p;
	}


	public Player getInitiator() {
		return initiator;
	}

}

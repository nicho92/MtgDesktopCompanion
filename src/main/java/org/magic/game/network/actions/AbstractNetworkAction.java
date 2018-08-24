package org.magic.game.network.actions;

import java.io.Serializable;

public abstract class AbstractNetworkAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ACTIONS {
		JOIN, REQUEST_PLAY, RESPONSE, CHANGE_DECK, SPEAK, LIST_PLAYER, CHANGE_STATUS, SHARE
	}

	private ACTIONS act;

	public ACTIONS getAct() {
		return act;
	}

	public void setAct(ACTIONS act) {
		this.act = act;
	}

}

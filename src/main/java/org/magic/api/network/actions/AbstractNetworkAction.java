package org.magic.api.network.actions;

import java.io.Serializable;

public abstract class AbstractNetworkAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ACTIONS {
		JOIN, CHANGE_DECK, SPEAK, LIST_PLAYER, CHANGE_STATUS
	}

	private ACTIONS act;

	public ACTIONS getAct() {
		return act;
	}

	public void setAct(ACTIONS act) {
		this.act = act;
	}

}

package org.magic.gui.game.network.actions;

import java.io.Serializable;

public abstract class AbstractGamingAction implements Serializable {

	public static enum ACTIONS {JOIN, PLAY,LEAVE,CHANGE_DECK,SPEAK,LIST_PLAYER};
	
	
	private ACTIONS act;
	
	
	public ACTIONS getAct() {
		return act;
	}
	
	public void setAct(ACTIONS act) {
		this.act = act;
	}
	
}

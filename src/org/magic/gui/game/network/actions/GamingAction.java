package org.magic.gui.game.network.actions;

import java.io.Serializable;

public  class GamingAction implements Serializable {

	public static enum ACTIONS {JOIN, PLAY,LEAVE,CHANGE_DECK};
	
	
	private ACTIONS act;
	private Object object;
	
	
	public ACTIONS getAct() {
		return act;
	}
	public void setAct(ACTIONS act) {
		this.act = act;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	
	
	public GamingAction() {
		// TODO Auto-generated constructor stub
	}
	
	public GamingAction(ACTIONS act, Object o)
	{
		this.act=act;
		this.object=o;
	}
	
	
	
}

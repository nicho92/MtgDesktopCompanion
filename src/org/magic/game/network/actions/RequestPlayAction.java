package org.magic.game.network.actions;

import org.magic.game.model.Player;

public class RequestPlayAction extends AbstractGamingAction {
	
	Player p1;
	Player p2;
	
	public RequestPlayAction(Player p1,Player p2) {
		setAct(ACTIONS.REQUEST_PLAY);
		this.p1=p1;
		this.p2=p2;
	}
	
	public Player getP1() {
		return p1;
	}
	
	public Player getP2() {
		return p2;
	}
	
	@Override
	public String toString() {
		return getP1() +" request " + getP2() + " to play a game"; 
	}

}

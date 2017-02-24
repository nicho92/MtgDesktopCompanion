package org.magic.gui.game.network.actions;

import org.magic.game.Player;

public class PlayAction extends AbstractGamingAction {
	
	Player p1;
	Player p2;
	
	public PlayAction(Player p1,Player p2) {
		setAct(ACTIONS.PLAY);
		this.p1=p1;
		this.p2=p2;
	}
	
	public Player getP1() {
		return p1;
	}
	
	public Player getP2() {
		return p2;
	}

}

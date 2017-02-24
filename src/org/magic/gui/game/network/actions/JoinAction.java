package org.magic.gui.game.network.actions;

import org.magic.game.Player;

public class JoinAction extends AbstractGamingAction{

	Player p1;
	
	public JoinAction(Player p1) {
		this.p1=p1;
		setAct(ACTIONS.JOIN);
		
	}

	public Player getPlayer() {
		return p1;
	}
	
	
	
}

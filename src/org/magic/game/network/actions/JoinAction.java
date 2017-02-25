package org.magic.game.network.actions;

import org.magic.game.model.Player;

public class JoinAction extends AbstractGamingAction{

	Player p1;
	
	public JoinAction(Player p1) {
		this.p1=p1;
		setAct(ACTIONS.JOIN);
		
	}

	public Player getPlayer() {
		return p1;
	}
	
	@Override
	public String toString() {
		return getPlayer() + " join the channel";
	}
	
	
	
}

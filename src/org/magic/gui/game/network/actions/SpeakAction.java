package org.magic.gui.game.network.actions;

import org.magic.game.Player;

public class SpeakAction extends AbstractGamingAction {

	Player p;
	String text;
	
	public SpeakAction(Player p, String text) {
		this.p=p;
		this.text=text;
		setAct(ACTIONS.SPEAK);
	}

	public Player getP() {
		return p;
	}

	public void setP(Player p) {
		this.p = p;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}

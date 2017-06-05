package org.magic.game.network.actions;

import org.magic.game.model.Player;
import java.awt.Color;

public class SpeakAction extends AbstractNetworkAction {

	Player p;
	String text;
	Color color;
	
	public SpeakAction(Player p, String text) {
		this.p=p;
		this.text=text;
		color=Color.BLACK;
		setAct(ACTIONS.SPEAK);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
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
	
	@Override
	public String toString() {
		if(getP()!=null)
			return getP() + " : " + getText();
		else
			return getText();
	}
	
	
	
}

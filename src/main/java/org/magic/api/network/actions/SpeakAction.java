package org.magic.api.network.actions;

import java.awt.Color;

import org.magic.game.model.Player;

public class SpeakAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text;
	private Color color;

	public SpeakAction(Player p, String text) {
		super(p);
		this.text = text;
		color = Color.BLACK;
		setAct(ACTIONS.SPEAK);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		if (getInitiator() != null)
			return getInitiator() + " : " + getText();
		else
			return getText();
	}

}

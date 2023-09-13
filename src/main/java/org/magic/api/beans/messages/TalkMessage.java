package org.magic.api.beans.messages;

import java.awt.Color;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.game.model.Player;

public class TalkMessage extends AbstractMessage{

	private static final long serialVersionUID = 1L;

	private String message;
	private Color color;
	
	public String toChatString() {
		return getAuthor().getName() + " : " + message;
	}
	
	public TalkMessage(Player author, String message, Color color) {
		super();
		setAuthor(author);
		this.message = message;
		this.color = color;
		
	}
	
	

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
		
}

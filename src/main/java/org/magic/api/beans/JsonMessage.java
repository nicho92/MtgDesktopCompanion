package org.magic.api.beans;

import java.awt.Color;
import java.util.Date;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.game.model.Player;
import org.magic.services.tools.CryptoUtils;

public class JsonMessage extends AbstractMessage{

	private static final long serialVersionUID = 1L;
	private Player author;
	private String message;
	private Color color;
	
	public String toChatString() {
		return author.getName() + " : " + message;
	}
	
	public JsonMessage(Player author, String message, Color color, MSG_TYPE typeMessage) {
		super();
		setId(CryptoUtils.generateMD5(author.getName()+new Date()+message+typeMessage));
		this.author = author;
		this.message = message;
		this.color = color;
		setTypeMessage(typeMessage);
	}
	
	public Player getAuthor() {
		return author;
	}
	public void setAuthor(Player author) {
		this.author = author;
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

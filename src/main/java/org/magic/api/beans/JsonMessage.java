package org.magic.api.beans;

import java.awt.Color;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.magic.api.interfaces.abstracts.extra.AbstractAuditableItem;
import org.magic.game.model.Player;
import org.magic.services.tools.CryptoUtils;

public class JsonMessage extends AbstractAuditableItem{

	private static final long serialVersionUID = 1L;
	private Player author;
	private String message;
	private Color color;
	private MSG_TYPE  typeMessage;
	private String id;
	
	public enum MSG_TYPE { CONNECT, CHANGESTATUS, DISCONNECT, TALK, SYSTEM, SEARCH, ANSWER}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	public String toChatString() {
		return author.getName() + " : " + message;
	}
	
	public JsonMessage() {
	}
	
	
	public JsonMessage(Player author, String message, Color color, MSG_TYPE typeMessage) {
		
		this.id = CryptoUtils.generateMD5(author.getName()+new Date()+message+typeMessage);
		this.author = author;
		this.message = message;
		this.color = color;
		this.typeMessage = typeMessage;
		setStart(Instant.now());
		setEnd(Instant.now());
		
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
	
	public MSG_TYPE getTypeMessage() {
		return typeMessage;
	}
	
	public void setTypeMessage(MSG_TYPE  typeMessage) {
		this.typeMessage = typeMessage;
	}


	public String getId() {
		return id;
	}

	
}

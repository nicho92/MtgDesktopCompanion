package org.magic.api.beans;

import java.awt.Color;
import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.magic.game.model.Player;

public class JsonMessage {

	private Player author;
	private long timeStamp;
	private String message;
	private Color color;
	private MSG_TYPE typeMessage;
	
	public enum MSG_TYPE { CONNECT, CHANGESTATUS,DISCONNECT,TALK,SYSTEM}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	public String toChatString() {
		return author.getName() + " : " + message;
	}
	
	public JsonMessage(Player author, String message, Color color, MSG_TYPE typeMessage) {
		this.author = author;
		this.message = message;
		this.color = color;
		this.typeMessage = typeMessage;
		this.timeStamp=Instant.now().toEpochMilli();
	}
	
	public Player getAuthor() {
		return author;
	}
	public void setAuthor(Player author) {
		this.author = author;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
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

	
}
